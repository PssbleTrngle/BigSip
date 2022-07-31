package com.possible_triangle.bigsip.data.generation

import com.possible_triangle.bigsip.BigSip
import com.possible_triangle.bigsip.block.GrapeCrop
import com.possible_triangle.bigsip.modules.GrapesModule
import com.possible_triangle.bigsip.modules.MaturingModule
import com.simibubi.create.content.logistics.block.vault.ItemVaultBlock
import net.minecraft.core.Direction
import net.minecraft.data.DataGenerator
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.CropBlock
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.client.model.generators.ConfiguredModel
import net.minecraftforge.common.data.ExistingFileHelper

class BlockModels(generator: DataGenerator, val fileHelper: ExistingFileHelper) :
    BlockStateProvider(generator, BigSip.MOD_ID, fileHelper) {

    private fun loc(name: ResourceLocation, map: (String) -> String): ResourceLocation {
        return ResourceLocation(name.namespace, map(name.path))
    }

    override fun registerStatesAndModels() {
        grapes()
        barrel()
    }

    private fun barrel() {
        val id = MaturingModule.BARREL.registryName ?: return
        val model = models().getExistingFile(ResourceLocation(id.namespace, "block/" + id.path))
        //val model = models().withExistingParent(id.path, ResourceLocation("block/barrel"))

        getVariantBuilder(MaturingModule.BARREL).forAllStates { state ->
            ConfiguredModel.builder().modelFile(model)
                .rotationY(if (state.getValue(ItemVaultBlock.HORIZONTAL_AXIS) === Direction.Axis.X) 90 else 0)
                .build()
        }
    }

    private fun grapes() {

        val name = GrapesModule.GRAPE_CROP.registryName!!
        val multipart = getMultipartBuilder(GrapesModule.GRAPE_CROP)

        val ages = 0 .. 7
        ages.forEach { age ->

            val mid = models().withExistingParent("${name.path}_mid_${age}", modLoc("block/grapes"))
            mid.texture("plant", modLoc("block/grapes_$age"))

            val side = models().withExistingParent("${name.path}_side_${age}", modLoc("block/grapes_connection"))
            val connection = age / 2
            side.texture("plant", modLoc("block/grapes_connection_$connection"))

            multipart.part().modelFile(mid)
                .addModel()
                .condition(CropBlock.AGE, age)

            GrapeCrop.PROPERTY_BY_DIRECTION.forEach { (dir, prop) ->
                multipart.part().modelFile(side)
                    .rotationY(dir.toYRot().toInt() % 360)
                    .uvLock(true)
                    .addModel()
                    .condition(prop, true)
                    .condition(CropBlock.AGE, age)
            }

        }

    }

}