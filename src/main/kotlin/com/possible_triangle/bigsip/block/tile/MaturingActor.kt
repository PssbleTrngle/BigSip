package com.possible_triangle.bigsip.block.tile

import com.possible_triangle.bigsip.modules.MaturingModule
import com.possible_triangle.bigsip.recipe.MaturingRecipe
import com.simibubi.create.foundation.utility.Lang
import com.simibubi.create.foundation.utility.LangBuilder
import net.minecraft.ChatFormatting
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.Container
import net.minecraft.world.level.Level
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.items.ItemStackHandler
import net.minecraftforge.items.wrapper.RecipeWrapper
import kotlin.math.ceil

interface IMaturingActor {
    val progress: Int
    val progressPercentage: Float
}

interface IMaturingContainer : IMaturingActor, Container {
    fun getFluid(): FluidStack
}

private const val checkEveryTicks = 60

abstract class MaturingActor : IMaturingContainer, RecipeWrapper(ItemStackHandler(0)) {
    final override var progress = 0
        private set

    val requiredDuration get() = recipe?.processingDuration ?: clientRequiredDuration

    override val progressPercentage
        get() = requiredDuration?.let {
            progress.toFloat() / it.toFloat()
        } ?: 0F

    fun createProgressBar(): LangBuilder? {
        val progress = progressPercentage
        if(progress <= 0) return null

        val percentage = (progress * 100F).toInt()
        val barWidth = 40
        val stripes = ceil(barWidth * progress).toInt()
        return Lang.builder()
            .text(" ")
            .text("|".repeat(stripes))
            .add(
                Lang.builder()
                    .text("|".repeat(barWidth - stripes))
                    .style(ChatFormatting.DARK_GRAY)
            )
            .text(" $percentage%")
    }

    private var clientRequiredDuration: Int? = null

    abstract fun setFluid(value: FluidStack)

    open fun onChange() {}

    private var recipe: MaturingRecipe? = null

    private fun potentialRecipe(world: ServerLevel): MaturingRecipe? {
        val recipes = world.recipeManager.getRecipesFor(MaturingModule.MATURING_RECIPE.get(), this, world)
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
        val shouldProgress = world.gameTime % checkEveryTicks == 0L

        if (world is ServerLevel) {
            if (shouldProgress) {
                recipe = potentialRecipe(world)
            }

            val currentRecipe = recipe
            if (currentRecipe != null) {
                if (progress >= currentRecipe.processingDuration) {

                    setFluid(currentRecipe.fluidResults.first().copy().also {
                        it.amount = fluidAmount
                    })
                    resetProgress()

                } else if (shouldProgress) {
                    progress += checkEveryTicks
                    onChange()
                }
            } else if (progress > 0) {
                resetProgress()
            }
        }
    }

    fun read(compound: CompoundTag, clientPacket: Boolean) {
        progress = compound.getInt("ProcessedAmount")
        if (clientPacket) clientRequiredDuration = compound.getInt("RequiredDuration")
    }

    fun write(compound: CompoundTag, clientPacket: Boolean) {
        compound.putInt("ProcessedAmount", progress)
        if (clientPacket) compound.putInt("RequiredDuration", recipe?.processingDuration ?: 0)
    }
}