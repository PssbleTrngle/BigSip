package com.possible_triangle.bigsip.data.generation.recipes

import com.possible_triangle.bigsip.BigSip
import com.possible_triangle.bigsip.Content
import com.possible_triangle.bigsip.recipe.MaturingRecipe
import com.simibubi.create.AllRecipeTypes
import com.simibubi.create.content.contraptions.fluids.actors.FillingRecipe
import com.simibubi.create.content.contraptions.processing.EmptyingRecipe
import com.simibubi.create.content.contraptions.processing.ProcessingRecipe
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder
import com.simibubi.create.foundation.data.recipe.ProcessingRecipeGen
import com.simibubi.create.foundation.utility.recipe.IRecipeTypeInfo
import net.minecraft.core.Registry
import net.minecraft.data.DataGenerator
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import net.minecraftforge.common.crafting.PartialNBTIngredient
import net.minecraftforge.fluids.FluidStack

fun ingredient(stack: ItemStack): Ingredient {
    return if (stack.tag?.isEmpty == false) PartialNBTIngredient.of(stack.item, stack.orCreateTag)
    else Ingredient.of(stack)
}

object CreateRecipes {

    fun register(generator: DataGenerator) {
        val filling = Gen<FillingRecipe>(AllRecipeTypes.FILLING, generator)
        val emptying = Gen<EmptyingRecipe>(AllRecipeTypes.EMPTYING, generator)
        val maturing = Gen<MaturingRecipe>(MaturingRecipe.INFO, generator)
        val compacting = Gen<MaturingRecipe>(AllRecipeTypes.COMPACTING, generator)

        val perPour = 250

        Content.DRINKS.forEach { drink ->
            val id = drink.registryName ?: return@forEach

            fun withDamage(damage: Int): ItemStack {
                return ItemStack(drink).apply {
                    damageValue = damage
                }
            }

            for (i in 0 until drink.uses) {
                val used = withDamage(i)
                val input = if (i == 0) ItemStack(Items.GLASS_BOTTLE) else withDamage(i)
                val pours = drink.uses - i

                val name = if (drink.uses > 1) "${id.path}_$i" else id.path
                filling.create(name) {
                    it.output(drink)
                        .require(drink.getFluid(), perPour * pours)
                        .require(ingredient(input))
                }

                emptying.create(name) {
                    it.require(ingredient(used))
                        .output(Items.GLASS_BOTTLE)
                        .output(drink.getFluid(), perPour * pours)
                }
            }
        }

        maturing.create("wine") {
            it.require(Content.GRAPE_JUICE.getFluid(), MaturingRecipe.DISPLAY_AMOUNT)
                .output(Content.WINE_BOTTLE.getFluid(), MaturingRecipe.DISPLAY_AMOUNT)
        }

        val juices = mapOf(
            "grapes" to Content.GRAPE_JUICE,
            "apple" to Content.APPLE_JUICE,
            "carrot" to Content.CARROT_JUICE,
        )

        val sugar = Items.SUGAR
        juices.forEach { (fruit, juice) ->
            val tag = TagKey.create(Registry.ITEM_REGISTRY, ResourceLocation("forge", "fruits/$fruit"))
            compacting.create(fruit) {
                it.output(FluidStack(juice.getFluid(), 250))
                    .require(tag)
                    .require(sugar)
            }
        }

    }
}

private open class Gen<T : ProcessingRecipe<*>>(private val type: IRecipeTypeInfo, generator: DataGenerator) :
    ProcessingRecipeGen(generator) {
    override fun getRecipeType() = type

    init {
        generator.addProvider(this)
    }

    fun create(
        name: String,
        transform: (ProcessingRecipeBuilder<T>) -> ProcessingRecipeBuilder<T>,
    ): GeneratedRecipe {
        return super.create(ResourceLocation(BigSip.MOD_ID, name), transform)
    }
}