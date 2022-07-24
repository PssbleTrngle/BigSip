package com.possible_triangle.bigsip.modules

import com.jozufozu.flywheel.core.PartialModel
import com.possible_triangle.bigsip.BigSip
import com.possible_triangle.bigsip.Registration
import com.possible_triangle.bigsip.block.MaturingBarrelBlock
import com.possible_triangle.bigsip.block.tile.MaturingBarrelTile
import com.possible_triangle.bigsip.data.generation.recipes.RecipeBuilder
import com.possible_triangle.bigsip.recipe.MaturingRecipe
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeSerializer
import com.simibubi.create.foundation.block.connected.AllCTTypes
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry
import com.simibubi.create.foundation.block.connected.CTSpriteShifter
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.block.entity.BlockEntityType
import thedarkcolour.kotlinforforge.forge.registerObject

object MaturingBarrel : Module {

    val BARREL by Registration.BLOCKS.registerObject("maturing_barrel") { MaturingBarrelBlock() }
    val BARREL_ITEM by Registration.ITEMS.registerObject("maturing_barrel") {
        BlockItem(BARREL,
            Registration.Properties)
    }
    val BARREL_TILE by Registration.TILES.registerObject("maturing_barrel") {
        BlockEntityType.Builder.of(::MaturingBarrelTile, BARREL).build(null)
    }

    val BARREL_CT_FRONT = createBarrelCT("front")
    val BARREL_CT_SIDE = createBarrelCT("side")
    val BARREL_CT_TOP = createBarrelCT("top")
    val BARREL_CT_BOTTOM = createBarrelCT("bottom")
    val BARREL_MULTIBLOCK = PartialModel(ResourceLocation(BigSip.MOD_ID, "block/multiblock_maturing_barrel"))

    private fun createBarrelCT(side: String): CTSpriteShiftEntry {
        return CTSpriteShifter.getCT(AllCTTypes.CROSS,
            ResourceLocation(BigSip.MOD_ID, "block/maturing_barrel_$side"),
            ResourceLocation(
                BigSip.MOD_ID, "block/maturing_barrel_${side}_connected"));
    }

    val MATURING_RECIPE = Registration.RECIPES.register(MaturingRecipe.ID) {
        Registration.createRecipeType<MaturingRecipe>(MaturingRecipe.ID)
    }

    val MATURING_RECIPE_SERIALIZER by Registration.RECIPE_SERIALIZERS.registerObject(MaturingRecipe.ID) {
        ProcessingRecipeSerializer(::MaturingRecipe)
    }

}