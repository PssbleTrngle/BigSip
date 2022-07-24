package com.possible_triangle.bigsip.recipe

import com.possible_triangle.bigsip.BigSip
import com.possible_triangle.bigsip.block.tile.IMaturingContainer
import com.possible_triangle.bigsip.modules.MaturingModule
import com.simibubi.create.content.contraptions.processing.ProcessingRecipe
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder.ProcessingRecipeParams
import com.simibubi.create.foundation.utility.recipe.IRecipeTypeInfo
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level

class MaturingRecipe(params: ProcessingRecipeParams) : ProcessingRecipe<IMaturingContainer>(INFO, params) {

    companion object {
        const val ID = "maturing"

        const val DISPLAY_AMOUNT = 1000

        val INFO = object : IRecipeTypeInfo {
            override fun getId() = ResourceLocation(BigSip.MOD_ID, ID)

            override fun <T : RecipeSerializer<*>> getSerializer(): T = MaturingModule.MATURING_RECIPE_SERIALIZER  as T

            override fun <T : RecipeType<*>> getType(): T = MaturingModule.MATURING_RECIPE.get() as T
        }
    }

    override fun getMaxInputCount() = 0

    override fun getMaxOutputCount() = 0

    override fun getMaxFluidInputCount() = 1

    override fun getMaxFluidOutputCount() = 1

    override fun matches(barrel: IMaturingContainer, world: Level): Boolean {
        val fluid = barrel.getFluid()
        if(fluid.isEmpty) return false
        return fluidIngredients.all { ingredient ->
            ingredient.test(fluid.copy().also {
                it.amount = DISPLAY_AMOUNT
            })
        }
    }

}