package com.possible_triangle.bigsip.modules

import com.possible_triangle.bigsip.Registration
import com.possible_triangle.bigsip.data.generation.TagBuilder
import com.possible_triangle.bigsip.data.generation.recipes.RecipeBuilder
import com.possible_triangle.bigsip.data.generation.recipes.ingredient
import com.possible_triangle.bigsip.item.Drink
import com.simibubi.create.AllRecipeTypes
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraftforge.fluids.FluidStack

object JuiceModule : Module {

    val APPLE_JUICE by Registration.withFluid("apple_juice") { Drink(it, 4, 0.5F) }
    val GRAPE_JUICE by Registration.withFluid("grape_juice") { Drink(it, 4, 0.5F) }

    private val UPRIGHT_ON_BELT = TagKey.create(Registry.ITEM_REGISTRY, ResourceLocation("create", "upright_on_belt"))

    private val JUICES
        get() = mapOf(
            "grapes" to GRAPE_JUICE,
            "apple" to APPLE_JUICE
        )

    override fun generateRecipes(builder: RecipeBuilder) {

        val sugar = Items.SUGAR
        JUICES.forEach { (fruit, juice) ->
            val tag = TagKey.create(Registry.ITEM_REGISTRY, ResourceLocation("forge", "fruits/$fruit"))
            builder.processing(AllRecipeTypes.COMPACTING, fruit) {
                output(FluidStack(juice.getFluid(), 250))
                for (i in 1..4) require(tag)
                require(sugar)
            }
        }

        Registration.DRINKS.forEach { drink ->
            val id = drink.registryName ?: return@forEach

            fun withDamage(damage: Int): ItemStack {
                return ItemStack(drink).apply {
                    damageValue = damage
                }
            }

            val perPour = 250

            for (i in 0 until drink.uses) {
                val used = withDamage(i)
                val input = if (i == 0) ItemStack(Items.GLASS_BOTTLE) else withDamage(i)
                val pours = drink.uses - i

                val name = if (drink.uses > 1) "${id.path}_$i" else id.path
                builder.processing(AllRecipeTypes.FILLING, name) {
                    output(drink)
                    require(drink.getFluid(), perPour * pours)
                    require(ingredient(input))
                }

                builder.thermal(name, "bottler") {
                    output(drink)
                    require(drink.getFluid(), perPour * pours)
                    require(input)
                }

                builder.processing(AllRecipeTypes.EMPTYING, name) {
                    output(Items.GLASS_BOTTLE)
                    output(drink.getFluid(), perPour * pours)
                    require(ingredient(used))
                }
            }
        }
    }

    override fun generateTags(builder: TagBuilder) {
        JUICES.forEach { (name, drink) ->
            val tag = TagKey.create(Registry.FLUID_REGISTRY, ResourceLocation("forge", "juices/$name"))
            builder.fluids.create(tag) {
                add(drink.getFluid())
            }
        }

        Registration.DRINKS.forEach { drink ->
            builder.items.create(UPRIGHT_ON_BELT) {
                add(drink)
            }
        }
    }

}