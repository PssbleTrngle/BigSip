package com.possible_triangle.bigsip.modules

import cofh.thermal.lib.compat.crt.base.CRTRecipe.IRecipeBuilder
import com.possible_triangle.bigsip.Registration
import com.possible_triangle.bigsip.config.Configs
import com.possible_triangle.bigsip.data.generation.recipes.RecipeBuilder
import com.possible_triangle.bigsip.effect.DizzinessEffect
import com.possible_triangle.bigsip.item.Alcohol
import com.possible_triangle.bigsip.recipe.MaturingRecipe
import com.simibubi.create.AllItems
import com.simibubi.create.AllRecipeTypes
import com.simibubi.create.content.contraptions.processing.HeatCondition
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.material.Fluids
import thedarkcolour.kotlinforforge.forge.registerObject

object Alcohol : Module {

    val WINE_BOTTLE by Registration.withFluid("wine", "wine_bottle") { Alcohol(it, 4, 0F, 5, uses = 3) }
    val BEER by Registration.withFluid("beer") { Alcohol(it, 4, 0.2F, 6, uses = 2) }
    val DARK_BEER by Registration.withFluid("dark_beer") { Alcohol(it, 4, 0.2F, 12, uses = 2) }

    val DIZZYNESS by Registration.EFFECTS.registerObject("dizziness") { DizzinessEffect() }

    val HOP by Registration.ITEMS.registerObject("hop") { Item(Registration.Properties) }
    val MASH = Registration.createFluid("mash")

    override fun addConditions(builder: IConditionBuilder) {
        listOf(WINE_BOTTLE, BEER, DARK_BEER).forEach {
            builder.register(it, Configs.SERVER.ENABLE_ALCOHOL::get)
            builder.register(it.getFluid(), Configs.SERVER.ENABLE_ALCOHOL::get)
        }
    }

    override fun generateRecipes(builder: RecipeBuilder) {

        builder.processing(AllRecipeTypes.MIXING, "mash") {
            output(MASH.get(), 1000)
            require(AllItems.WHEAT_FLOUR.get())
            require(AllItems.WHEAT_FLOUR.get())
            require(HOP)
            require(Items.BROWN_MUSHROOM)
            require(Fluids.WATER, 1000)
            requiresHeat(HeatCondition.HEATED)
        }

        builder.processing(MaturingRecipe.INFO, "beer") {
            output(BEER.getFluid(), MaturingRecipe.DISPLAY_AMOUNT)
            require(MASH.get(), MaturingRecipe.DISPLAY_AMOUNT)
            duration(20 * 60 * 10)
        }


        builder.processing(MaturingRecipe.INFO, "wine") {
            output(WINE_BOTTLE.getFluid(), MaturingRecipe.DISPLAY_AMOUNT)
            require(Juices.GRAPE_JUICE.getFluid(), MaturingRecipe.DISPLAY_AMOUNT)
            duration(20 * 60 * 10)
        }

    }

}