package com.possible_triangle.bigsip.modules

import com.possible_triangle.bigsip.Registration
import com.possible_triangle.bigsip.compat.ModCompat.Mod
import com.possible_triangle.bigsip.config.Configs
import com.possible_triangle.bigsip.data.generation.TagBuilder
import com.possible_triangle.bigsip.data.generation.recipes.RecipeBuilder
import com.possible_triangle.bigsip.data.generation.recipes.ingredient
import com.possible_triangle.bigsip.item.Drink
import com.possible_triangle.bigsip.recipe.ConfigCondition
import com.simibubi.create.AllRecipeTypes
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.material.Fluids
import net.minecraftforge.common.crafting.conditions.AndCondition
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition
import net.minecraftforge.common.crafting.conditions.NotCondition
import net.minecraftforge.fluids.FluidStack
import toughasnails.api.item.TANItems

object JuiceModule : ModModule {

    val APPLE_JUICE by Registration.withFluid("apple_juice") { Drink(it, 4, 0.5F) }
    val GRAPE_JUICE by Registration.withFluid("grape_juice") { Drink(it, 4, 0.5F) }

    val UPRIGHT_ON_BELT = TagKey.create(Registry.ITEM_REGISTRY, ResourceLocation("create", "upright_on_belt"))

    private val JUICES
        get() = mapOf("grapes" to GRAPE_JUICE, "apple" to APPLE_JUICE)

    override fun generateRecipes(builder: RecipeBuilder) {

        val sugar = Items.SUGAR
        JUICES.forEach { (fruit, juice) ->
            val tag = Registration.fruitTag(fruit)
            val pureWaterCondition = AndCondition(
                ModLoadedCondition(Mod.TAN.id),
                ConfigCondition(Configs.SERVER.TAN_PURE_WATER_COMPAT),
                ConfigCondition(Configs.SERVER.TAN_JUICE_PURE_WATER)
            )

            fun ProcessingRecipeBuilder<*>.juiceRecipe() {
                output(FluidStack(juice.getFluid(), 750))
                for (i in 1..3) require(tag)
                require(sugar)
            }

            builder.processing(AllRecipeTypes.COMPACTING, "${fruit}_juice") {
                juiceRecipe()
                require(Fluids.WATER, 500)
                withCondition(NotCondition(pureWaterCondition))
            }

            builder.processing(AllRecipeTypes.COMPACTING, "${Mod.TAN}/${fruit}_juice") {
                juiceRecipe()
                require(FluidCompatModule.PURE_WATER_TAG, 500)
                withCondition(pureWaterCondition)
            }
        }

        mapOf(TANItems.APPLE_JUICE to APPLE_JUICE).forEach { (bottle, drink) ->
            val fluid = drink.getFluid()
            val id = drink.registryName ?: return@forEach

            builder.processing(AllRecipeTypes.EMPTYING, "${Mod.TAN}/${id.path}") {
                output(fluid, 250)
                output(Items.GLASS_BOTTLE)
                require(bottle)
                withCondition(ModLoadedCondition(Mod.TAN.id))
                withCondition(ConfigCondition(Configs.SERVER.TAN_JUICE_COMPAT))
            }
        }

        Registration.DRINKS.forEach { drink ->
            val id = drink.registryName ?: return@forEach

            val perPour = 250

            for (i in 0 until drink.uses) {
                val used = drink.withDamage(i)
                val input = if (i == 0) ItemStack(Items.GLASS_BOTTLE) else drink.withDamage(i)
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
            val tag = Registration.juiceFluidTag(name)
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