package com.possible_triangle.bigsip.compat.jei

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Vector3f
import com.possible_triangle.bigsip.BigSip.MOD_ID
import com.possible_triangle.bigsip.modules.Grapes
import com.possible_triangle.bigsip.modules.MaturingBarrel
import com.possible_triangle.bigsip.recipe.MaturingRecipe
import com.simibubi.create.compat.jei.category.CreateRecipeCategory
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics
import com.simibubi.create.foundation.gui.AllGuiTextures
import com.simibubi.create.foundation.gui.element.GuiGameElement
import mezz.jei.api.constants.VanillaTypes
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeIngredientRole
import mezz.jei.api.recipe.RecipeType
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TranslatableComponent
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object MaturingCategory : CreateRecipeCategory<MaturingRecipe>(doubleItemIcon(MaturingBarrel.BARREL_ITEM, Grapes.GRAPES),
    emptyBackground(177, 70)) {

    override fun getRecipeClass() = MaturingRecipe::class.java

    init {
        name = MaturingRecipe.ID
        type = RecipeType.create(MOD_ID, name, recipeClass)
    }

    override fun getTitle(): Component = TranslatableComponent("$MOD_ID.recipe.$name")

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: MaturingRecipe, focuses: IFocusGroup) {
        val input = recipe.fluidIngredients.getOrNull(0) ?: return

        builder.addSlot(RecipeIngredientRole.INPUT, 27, 36).setBackground(getRenderedSlot(), -1, -1)
            .addIngredients(VanillaTypes.FLUID, input.getMatchingFluidStacks())
            .addTooltipCallback(addFluidTooltip(MaturingRecipe.DISPLAY_AMOUNT))

        builder.addSlot(RecipeIngredientRole.OUTPUT, 132, 36).setBackground(getRenderedSlot(), -1, -1)
            .addIngredients(VanillaTypes.FLUID, recipe.fluidResults)
            .addTooltipCallback(addFluidTooltip(MaturingRecipe.DISPLAY_AMOUNT))
    }


    override fun draw(
        recipe: MaturingRecipe,
        recipeSlotsView: IRecipeSlotsView,
        matrixStack: PoseStack,
        mouseX: Double,
        mouseY: Double,
    ) {
        AllGuiTextures.JEI_SHADOW.render(matrixStack, 58, 42)
        AllGuiTextures.JEI_SHADOW.render(matrixStack, 68, 34)
        AllGuiTextures.JEI_DOWN_ARROW.render(matrixStack, 126, 19)
        renderBarrel(matrixStack)

        val duration = (recipe.processingDuration * 50).milliseconds
        val text = TranslatableComponent("${MOD_ID}.recipe.maturing.time", duration)
        Minecraft.getInstance().font.draw(matrixStack, text, 26.0f, 60.0f, 0xFFFFFF)
    }

    private fun renderBarrel(matrixStack: PoseStack) {
        matrixStack.pushPose()
        matrixStack.translate(80.0, 41.0, 100.0)
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-12.5f))
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-18.5f))
        val scale = 16.0
        GuiGameElement.of(MaturingBarrel.BARREL_MULTIBLOCK).lighting(AnimatedKinetics.DEFAULT_LIGHTING).scale(scale)
            .render(matrixStack)
        matrixStack.popPose()
    }

}