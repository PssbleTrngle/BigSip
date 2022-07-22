package com.possible_triangle.bigsip.compat.jei

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Vector3f
import com.possible_triangle.bigsip.BigSip.MOD_ID
import com.possible_triangle.bigsip.Content
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
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TranslatableComponent

object MaturingCategory :
    CreateRecipeCategory<MaturingRecipe>(doubleItemIcon(Content.BARREL_ITEM, Content.GRAPES),
        emptyBackground(177, 70)) {

    override fun getRecipeClass() = MaturingRecipe::class.java

    init {
        name = MaturingRecipe.ID
        type = RecipeType.create(MOD_ID, name, recipeClass)
    }

    override fun getTitle(): Component = TranslatableComponent("$MOD_ID.recipe.$name")

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: MaturingRecipe, focuses: IFocusGroup) {
        val input = recipe.fluidIngredients.getOrNull(0) ?: return

        builder.addSlot(RecipeIngredientRole.INPUT, 27, 51)
            .setBackground(getRenderedSlot(), -1, -1)
            .addIngredients(VanillaTypes.FLUID, input.getMatchingFluidStacks())
            .addTooltipCallback(addFluidTooltip(MaturingRecipe.DISPLAY_AMOUNT))

        builder.addSlot(RecipeIngredientRole.OUTPUT, 132, 51)
            .setBackground(getRenderedSlot(), -1, -1)
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
        AllGuiTextures.JEI_SHADOW.render(matrixStack, 62, 57)
        AllGuiTextures.JEI_DOWN_ARROW.render(matrixStack, 126, 29)
        renderBarrel(matrixStack)
    }

    private fun renderBarrel(matrixStack: PoseStack) {
        val state = Content.BARREL.defaultBlockState()
        matrixStack.pushPose()
        matrixStack.translate(74.0, 51.0, 100.0)
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-15.5f))
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(22.5f))
        val scale = 20.0
        GuiGameElement.of(state).lighting(AnimatedKinetics.DEFAULT_LIGHTING).scale(scale).render(matrixStack)
        matrixStack.popPose()
    }

}