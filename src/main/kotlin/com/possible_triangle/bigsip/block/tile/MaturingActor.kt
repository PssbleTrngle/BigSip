package com.possible_triangle.bigsip.block.tile

import com.possible_triangle.bigsip.Content
import com.possible_triangle.bigsip.recipe.MaturingRecipe
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.Container
import net.minecraft.world.level.Level
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.items.ItemStackHandler
import net.minecraftforge.items.wrapper.RecipeWrapper
import kotlin.math.max

fun interface IMaturingActor {
    fun getProgress(): Int
}

interface IMaturingContainer : IMaturingActor, Container {
    fun getFluid(): FluidStack
}

abstract class MaturingActor : IMaturingContainer, RecipeWrapper(ItemStackHandler(0)) {
    private var progress = 0

    override fun getProgress(): Int = progress

    abstract fun setFluid(value: FluidStack)

    open fun onChange() {}

    private var recipe: MaturingRecipe? = null

    private fun potentialRecipe(world: ServerLevel): MaturingRecipe? {
        val recipes = world.recipeManager.getRecipesFor(Content.MATURING_RECIPE.get(), this, world)
        return recipes.firstOrNull()
    }

    private fun resetProgress() {
        progress = 0
        onChange()
    }

    fun onFluidChanged(value: FluidStack) {
        val fluid = getFluid()
        if (!fluid.isFluidEqual(value)) {
            resetProgress()
        }
    }

    fun tick(world: Level) {
        val fluidAmount = getFluid().amount
        val currentRecipe = recipe
        val shouldProgress = world.gameTime % 60 == 0L

        if (world is ServerLevel) {
            if (shouldProgress) {
                recipe = potentialRecipe(world)
            }

            if (currentRecipe != null) {
                if (progress >= fluidAmount) {
                    setFluid(currentRecipe.fluidResults.first().copy().also {
                        it.amount = fluidAmount
                    })
                    resetProgress()
                } else if (shouldProgress) {
                    progress += max(1, fluidAmount / 100)
                    onChange()
                }
            } else if (progress > 0) {
                resetProgress()
            }
        }
    }

    fun read(compound: CompoundTag) {
        progress = compound.getInt("ProcessedAmount")
    }

    fun write(compound: CompoundTag) {
        compound.putInt("ProcessedAmount", progress)
    }
}