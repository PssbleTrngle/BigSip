package com.possible_triangle.bigsip.data.generation.recipes

import com.possible_triangle.bigsip.modules.Grapes
import net.minecraft.data.DataGenerator
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraftforge.common.Tags
import java.util.function.Consumer

class VanillaRecipes(generator: DataGenerator) : RecipeProvider(generator) {

    override fun buildCraftingRecipes(registration: Consumer<FinishedRecipe>) {

        ShapelessRecipeBuilder.shapeless(Grapes.GRAPE_SAPLING)
            .requires(Tags.Items.RODS_WOODEN)
            .requires(Grapes.GRAPES)
            .cr
            .save(registration)

    }

}