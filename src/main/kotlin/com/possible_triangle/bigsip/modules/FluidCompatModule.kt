package com.possible_triangle.bigsip.modules

import com.possible_triangle.bigsip.BigSip
import com.possible_triangle.bigsip.Registration
import com.possible_triangle.bigsip.compat.ModCompat
import com.possible_triangle.bigsip.config.Configs
import com.possible_triangle.bigsip.data.generation.TagBuilder
import com.possible_triangle.bigsip.data.generation.recipes.RecipeBuilder
import com.possible_triangle.bigsip.data.generation.recipes.ingredient
import com.possible_triangle.bigsip.recipe.ConfigCondition
import com.simibubi.create.AllRecipeTypes
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.material.Fluid
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

        fun canteen(filled: Item, fluid: Fluid) {
            val id = filled.registryName?.path ?: return
            builder.processing(AllRecipeTypes.FILLING, "${ModCompat.Mod.TAN}/${id}_0") {
                output(filled.withDamage(2))
                require(fluid, 750)
                require(TANItems.EMPTY_CANTEEN)
                condition()
            }

            builder.processing(AllRecipeTypes.FILLING, "${ModCompat.Mod.TAN}/${id}_1") {
                output(filled)
                require(fluid, 500)
                require(ingredient(filled.withDamage(2)))
                condition()
            }

            for (uses in 0..4) {
                val amount = (5 - uses) * 250
                builder.processing(AllRecipeTypes.EMPTYING, "${ModCompat.Mod.TAN}/${id}_$uses") {
                    output(TANItems.EMPTY_CANTEEN)
                    output(fluid, amount)
                    require(ingredient(filled.withDamage(uses)))
                    condition()
                }
            }
        }

        canteen(TANItems.WATER_CANTEEN, Fluids.WATER)
        canteen(TANItems.PURIFIED_WATER_CANTEEN, PURE_WATER.get())
    }

    override fun generateTags(builder: TagBuilder) {
        builder.fluids.create(PURE_WATER_TAG) {
            add(PURE_WATER.get())
        }

        builder.items.create(JuiceModule.UPRIGHT_ON_BELT) {
            add(TANItems.PURIFIED_WATER_BOTTLE)
            add(TANItems.DIRTY_WATER_BOTTLE)

            add(TANItems.EMPTY_CANTEEN)
            add(TANItems.WATER_CANTEEN)
            add(TANItems.PURIFIED_WATER_CANTEEN)
            add(TANItems.DIRTY_WATER_CANTEEN)

            add(TANItems.APPLE_JUICE)
            add(TANItems.CACTUS_JUICE)
            add(TANItems.CHORUS_FRUIT_JUICE)
            add(TANItems.MELON_JUICE)
            add(TANItems.GLOW_BERRY_JUICE)
            add(TANItems.PUMPKIN_JUICE)
            add(TANItems.SWEET_BERRY_JUICE)
        }
    }
}