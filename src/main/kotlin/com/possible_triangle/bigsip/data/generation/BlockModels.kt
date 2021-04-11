package com.possible_triangle.bigsip.data.generation

import com.possible_triangle.bigsip.BigSip
import com.possible_triangle.bigsip.Content
import com.possible_triangle.bigsip.block.GrapeCrop
import net.minecraft.block.CropsBlock
import net.minecraft.data.DataGenerator
import net.minecraft.util.Direction
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.common.data.ExistingFileHelper
import kotlin.math.floor

class BlockModels(generator: DataGenerator, fileHelper: ExistingFileHelper) :
    BlockStateProvider(generator, BigSip.MOD_ID, fileHelper) {

    private fun loc(name: ResourceLocation, map: (String) -> String): ResourceLocation {
        return ResourceLocation(name.namespace, map(name.path))
    }

    override fun registerStatesAndModels() {

        val name = Content.GRAPE_CROP.registryName!!

        val offset = 6F

        val multipart = getMultipartBuilder(Content.GRAPE_CROP)

        val ages = 0 until 7
        ages.forEach { age ->

            val mid = models().withExistingParent("${name.path}_mid_${age}", mcLoc("block/block"))

            listOf("lower", "upper").forEachIndexed { i, half ->
                mid.texture(half, loc(name) { "block/${it}_${age}_${half}" })
                mid.texture(half, mcLoc("block/vine"))
                mid.texture("post", mcLoc("block/oak_planks"))

                val start = 16F * i - 1F
                val height = if (i == 0) 16F else 6F
                val end = start + height
                val postSize = 3F

                mid.element()
                    .from(8F - postSize / 2, start, 8F - postSize / 2)
                    .to(8F + postSize / 2, end + 2F, 8F + postSize / 2)
                    .allFaces { dir, face ->
                        face.texture("#post").cullface(dir.opposite)
                        val postStart = floor(8F - postSize / 2)
                        val postEnd = floor(8F + postSize / 2)
                        if (dir.axis.isHorizontal) face.uvs(postStart, 0F, postEnd, height + 2F)
                        else face.uvs(postStart, postStart, postEnd, postEnd)
                    }

                val east = mid.element()
                    .from(offset, start, offset / 2)
                    .to(offset, end, 16 - offset / 2)

                val west = mid.element()
                    .from(16F - offset, start, offset / 2)
                    .to(16F - offset, end, 16F - offset / 2)

                val north = mid.element()
                    .from(offset / 2, start, offset)
                    .to(16 - offset / 2, end, offset)

                val south = mid.element()
                    .from(offset / 2, start, 16F - offset)
                    .to(16 - offset / 2, end, 16F - offset)

                listOf(Direction.NORTH, Direction.SOUTH).forEach { dir ->
                    listOf(north, south).forEach {
                        it.face(dir)
                            .uvs(0F, 0F, 16F - offset, height)
                            .texture("#$half")
                    }
                }

                listOf(Direction.EAST, Direction.WEST).forEach { dir ->
                    listOf(east, west).forEach {
                        it.face(dir)
                            .uvs(0F, 0F, 16F - offset, height)
                            .texture("#$half")
                    }
                }
            }

            val side = models().withExistingParent("${name.path}_side_${age}", mcLoc("block/block"))

            multipart.part().modelFile(mid)
                .addModel()
                .condition(CropsBlock.AGE, age + 1)

            GrapeCrop.PROPERTY_BY_DIRECTION.forEach { (dir, prop) ->
                multipart.part().modelFile(side)
                    .rotationY((dir.toYRot().toInt() + 180) % 360)
                    .uvLock(true)
                    .addModel()
                    .condition(prop, true)
                    .condition(CropsBlock.AGE, age + 1)
            }

        }

    }

}