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
import kotlin.math.floor

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
        val offset = 0F
        val offset2 = 6F
        val multipart = getMultipartBuilder(GrapesModule.GRAPE_CROP)

        val ages = 0 .. 7
        ages.forEach { age ->

            val mid = models().withExistingParent("${name.path}_mid_${age}", mcLoc("block/block"))

            listOf("lower", "upper").forEachIndexed { i, half ->
                mid.texture("post", mcLoc("block/oak_planks"))
                val grapeTexture = loc(name) { "block/${it}_${age}_${half}" }
                //val grapeTexture = mcLoc("block/spruce_planks")
                mid.texture(half, grapeTexture)
                mid.texture("particle", grapeTexture)

                val start = 16F * i - 1F
                val height = if (i == 0) 16F else 8F

                val end = start + height
                val postSize = 2F
                mid.element()
                    .from(8F - postSize / 2, start, 8F - postSize / 2)
                    .to(8F + postSize / 2, end, 8F + postSize / 2)
                    .allFaces { dir, face ->
                        face.texture("#post").cullface(dir.opposite)
                        val postStart = floor(8F - postSize / 2)
                        val postEnd = floor(8F + postSize / 2)
                        if (dir.axis.isHorizontal) face.uvs(postStart, 0F, postEnd, height)
                        else face.uvs(postStart, postStart, postEnd, postEnd)
                    }

                val east = mid.element()
                    .from(offset2, start, offset / 2)
                    .to(offset2, start + 16F, 16 - offset / 2)

                val west = mid.element()
                    .from(16F - offset2, start, offset / 2)
                    .to(16F - offset2, start + 16F, 16F - offset / 2)

                val north = mid.element()
                    .from(offset / 2, start, offset2)
                    .to(16 - offset / 2, start + 16F, offset2)

                val south = mid.element()
                    .from(offset / 2, start, 16F - offset2)
                    .to(16 - offset / 2, start + 16F, 16F - offset2)

                listOf(Direction.NORTH, Direction.SOUTH).forEach { dir ->
                    listOf(north, south).forEach {
                        it.face(dir)
                            .uvs(0F, 0F, 16F, 16F)
                            .texture("#$half")
                    }
                }

                listOf(Direction.EAST, Direction.WEST).forEach { dir ->
                    listOf(east, west).forEach {
                        it.face(dir)
                            .uvs(0F, 0F, 16F, 16F)
                            .texture("#$half")
                    }
                }
            }

            val side = models().withExistingParent("${name.path}_side_${age}", mcLoc("block/block"))

            multipart.part().modelFile(mid)
                .addModel()
                .condition(CropBlock.AGE, age)

            GrapeCrop.PROPERTY_BY_DIRECTION.forEach { (dir, prop) ->
                multipart.part().modelFile(side)
                    .rotationY((dir.toYRot().toInt() + 180) % 360)
                    .uvLock(true)
                    .addModel()
                    .condition(prop, true)
                    .condition(CropBlock.AGE, age)
            }

        }

    }

}