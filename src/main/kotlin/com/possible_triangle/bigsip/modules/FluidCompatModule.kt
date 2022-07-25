package com.possible_triangle.bigsip.modules

import com.possible_triangle.bigsip.BigSip
import com.possible_triangle.bigsip.Registration
import com.possible_triangle.bigsip.compat.ModCompat
import com.possible_triangle.bigsip.config.Configs
import com.possible_triangle.bigsip.data.generation.TagBuilder
import com.possible_triangle.bigsip.data.generation.recipes.RecipeBuilder
import com.possible_triangle.bigsip.recipe.ConfigCondition
import com.simibubi.create.AllRecipeTypes
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Items
import net.minecraft.world.level.material.Fluids
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition
import toughasnails.api.item.TANItems

object FluidCompatModule : ModModule {

    private val PURE_WATER = Registration.createFluid("purified_water")
    val PURE_WATER_TAG = TagKey.create(Registry.FLUID_REGISTRY, ResourceLocation(BigSip.MOD_ID, "water/purified"))

    override fun addConditions(builder: IConditionBuilder) {
        builder.register(PURE_WATER.get()) { it.isLoaded(ModCompat.Mod.TAN) }
    }

    override fun generateRecipes(builder: RecipeBuilder) {
        fun ProcessingRecipeBuilder<*>.condition() {
            withCondition(ModLoadedCondition(ModCompat.Mod.TAN.id))
            withCondition(ConfigCondition(Configs.SERVER.TAN_PURE_WATER_COMPAT))
        }

        builder.processing(AllRecipeTypes.MIXING, "${ModCompat.Mod.TAN}/purified_water") {
            output(PURE_WATER.get(), 250)
            require(Fluids.WATER, 250)
            condition()
        }

        builder.processing(AllRecipeTypes.EMPTYING, "${ModCompat.Mod.TAN}/purified_water_bottle") {
            output(PURE_WATER.get(), 250)
            output(Items.GLASS_BOTTLE)
            require(TANItems.PURIFIED_WATER_BOTTLE)
            condition()
        }

        builder.processing(AllRecipeTypes.FILLING, "${ModCompat.Mod.TAN}/purified_water_bottle") {
            output(TANItems.PURIFIED_WATER_BOTTLE)
            require(PURE_WATER_TAG, 250)
            require(Items.GLASS_BOTTLE)
            condition()
        }

        builder.processing(AllRecipeTypes.FILLING, "${ModCompat.Mod.TAN}/purified_water_canteen") {
            output(TANItems.PURIFIED_WATER_CANTEEN)
            require(PURE_WATER_TAG, 250)
            require(TANItems.EMPTY_CANTEEN)
            condition()
        }
    }

    override fun generateTags(builder: TagBuilder) {
        builder.fluids.create(PURE_WATER_TAG) {
            add(PURE_WATER.get())
        }
    }
}