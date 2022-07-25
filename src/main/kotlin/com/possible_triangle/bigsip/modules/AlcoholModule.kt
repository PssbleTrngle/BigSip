package com.possible_triangle.bigsip.modules

import com.possible_triangle.bigsip.BigSip.MOD_ID
import com.possible_triangle.bigsip.Registration
import com.possible_triangle.bigsip.config.Configs
import com.possible_triangle.bigsip.data.generation.TagBuilder
import com.possible_triangle.bigsip.data.generation.recipes.RecipeBuilder
import com.possible_triangle.bigsip.effect.DizzinessEffect
import com.possible_triangle.bigsip.item.Alcohol
import com.possible_triangle.bigsip.recipe.MaturingRecipe
import com.simibubi.create.AllItems
import com.simibubi.create.AllRecipeTypes
import com.simibubi.create.content.contraptions.processing.HeatCondition
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.material.Fluids
import net.minecraftforge.common.crafting.conditions.NotCondition
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition
import thedarkcolour.kotlinforforge.forge.registerObject

object AlcoholModule : ModModule {

    val WINE_BOTTLE by Registration.withFluid("wine", "wine_bottle") { Alcohol(it, 4, 0F, 11, uses = 3) }
    val BEER by Registration.withFluid("beer") { Alcohol(it, 4, 0.2F, 5, uses = 2) }
    val DARK_BEER by Registration.withFluid("dark_beer") { Alcohol(it, 4, 0.2F, 5, uses = 2) }
    val APPLE_WINE by Registration.withFluid("apple_wine") { Alcohol(it, 4, 0.2F, 6, uses = 2) }

    val DIZZYNESS by Registration.EFFECTS.registerObject("dizziness") { DizzinessEffect() }

    private val HOPS by Registration.ITEMS.registerObject("hops") { Item(Registration.Properties) }
    val HOPS_TAG = Registration.cropTag("hops")
    private val MASH = Registration.createFluid("mash")
    private val DARK_MASH = Registration.createFluid("dark_mash")

    private val BITTERNESS_FACTOR = TagKey.create(Registry.ITEM_REGISTRY, ResourceLocation(MOD_ID, "bitterness_factor"))
    private val BARLEY = TagKey.create(Registry.ITEM_REGISTRY, ResourceLocation("forge", "crops/barley"))

    override fun addConditions(builder: IConditionBuilder) {
        listOf(WINE_BOTTLE, BEER, DARK_BEER).forEach {
            builder.register(it) { Configs.SERVER.ENABLE_ALCOHOL.get() }
            builder.register(it.getFluid()) { Configs.SERVER.ENABLE_ALCOHOL.get() }
        }

        builder.register(HOPS) { !it.isLoaded("thermal_cultivation") }
        builder.register(DARK_BEER) { !it.tagEmpty(BARLEY, Registry.ITEM_REGISTRY) }
        builder.register(DARK_BEER.getFluid()) { !it.tagEmpty(BARLEY, Registry.ITEM_REGISTRY) }
    }

    override fun generateRecipes(builder: RecipeBuilder) {

        fun ProcessingRecipeBuilder<*>.mashIngredients() {
            require(BITTERNESS_FACTOR)
            require(Items.BROWN_MUSHROOM)
            require(Fluids.WATER, 1000)
            requiresHeat(HeatCondition.HEATED)
        }

        builder.processing(AllRecipeTypes.MIXING, "mash") {
            output(MASH.get(), 1000)
            mashIngredients()
            require(AllItems.WHEAT_FLOUR.get())
            require(AllItems.WHEAT_FLOUR.get())
        }

        builder.processing(AllRecipeTypes.MIXING, "dark_mash") {
            output(DARK_MASH.get(), 1000)
            mashIngredients()
            require(BARLEY)
            require(BARLEY)
            withCondition(NotCondition(TagEmptyCondition(BARLEY.location)))
        }

        builder.processing(MaturingRecipe.INFO, "beer") {
            output(BEER.getFluid(), MaturingRecipe.DISPLAY_AMOUNT)
            require(MASH.get(), MaturingRecipe.DISPLAY_AMOUNT)
            duration(20 * 60 * 10)
        }

        builder.processing(MaturingRecipe.INFO, "dark_beer") {
            output(DARK_BEER.getFluid(), MaturingRecipe.DISPLAY_AMOUNT)
            require(DARK_MASH.get(), MaturingRecipe.DISPLAY_AMOUNT)
            duration(20 * 60 * 10)
        }

        builder.processing(MaturingRecipe.INFO, "wine") {
            output(WINE_BOTTLE.getFluid(), MaturingRecipe.DISPLAY_AMOUNT)
            require(Registration.juiceFluidTag("grapes"), MaturingRecipe.DISPLAY_AMOUNT)
            duration(20 * 60 * 10)
        }

        builder.processing(MaturingRecipe.INFO, "apple_wine") {
            output(APPLE_WINE.getFluid(), MaturingRecipe.DISPLAY_AMOUNT)
            require(Registration.juiceFluidTag("apple"), MaturingRecipe.DISPLAY_AMOUNT)
            duration(20 * 60 * 6)
        }

    }

    override fun generateTags(builder: TagBuilder) {
        builder.items.create(HOPS_TAG) {
            add(HOPS)
        }

        builder.items.create(BITTERNESS_FACTOR) {
            addTag(HOPS_TAG)
            add(Items.DANDELION)
        }
    }

}