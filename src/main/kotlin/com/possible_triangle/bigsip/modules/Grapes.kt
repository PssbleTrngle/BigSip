package com.possible_triangle.bigsip.modules

import com.possible_triangle.bigsip.Registration
import com.possible_triangle.bigsip.block.GrapeCrop
import com.possible_triangle.bigsip.data.generation.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemNameBlockItem
import net.minecraftforge.common.Tags
import thedarkcolour.kotlinforforge.forge.registerObject

object Grapes : Module {

    val GRAPES by Registration.ITEMS.registerObject("grapes") { Item(Registration.Properties) }
    val GRAPE_CROP by Registration.BLOCKS.registerObject("grapes") { GrapeCrop() }
    val GRAPE_SAPLING by Registration.ITEMS.registerObject("grape_sapling") {
        ItemNameBlockItem(GRAPE_CROP,
            Registration.Properties)
    }

    override fun generateRecipes(builder: RecipeBuilder) {
        with(builder) {
            shapeless(GRAPE_SAPLING) {
                requires(Tags.Items.RODS_WOODEN)
                requires(GRAPES)
                unlockedBy("has_grapes", hasItem(GRAPES))
            }
        }
    }

}