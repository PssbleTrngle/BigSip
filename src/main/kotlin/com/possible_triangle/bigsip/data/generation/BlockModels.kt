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

class BlockModels(generator: DataGenerator, fileHelper: ExistingFileHelper) :
    BlockStateProvider(generator, BigSip.MOD_ID, fileHelper) {

    private fun loc(name: ResourceLocation, map: (String) -> String): ResourceLocation {
        return ResourceLocation(name.namespace, map(name.path))
    }

    override fun registerStatesAndModels() {

        val name = Content.GRAPE_CROP.registryName!!

        val offset = 4F

        val multipart = getMultipartBuilder(Content.GRAPE_CROP)

        val ages = 0 until 7
        ages.forEach { age ->

            val mid = models().withExistingParent("${name.path}_mid_${age}", mcLoc("block/block"))

            listOf("upper", "lower").forEachIndexed { i, half ->
                listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST).forEach { dir ->
                    mid.texture(half, loc(name) { "block/${it}_${age}_${half}" })
                        .element()
                        .from(offset, 16F * i, offset)
                        .to(16F - offset, 16F * (i + 1), 16F - offset)
                        .face(dir).uvs(0F, 0F, 16F, 32F).texture("#$half")
                }
            }

            val side = models().withExistingParent("${name.path}_side_${age}", mcLoc("block/block"))

            multipart.part().modelFile(mid)
                .addModel()
                .condition(CropsBlock.AGE, age)

            GrapeCrop.PROPERTY_BY_DIRECTION.forEach { (dir, prop) ->
                multipart.part().modelFile(side)
                    .rotationY((dir.toYRot().toInt() + 180) % 360)
                    .uvLock(true)
                    .addModel()
                    .condition(prop, true)
                    .condition(CropsBlock.AGE, age)
            }

        }

    }

}