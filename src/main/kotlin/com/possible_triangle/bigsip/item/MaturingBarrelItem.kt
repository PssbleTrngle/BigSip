package com.possible_triangle.bigsip.item

import com.possible_triangle.bigsip.block.tile.MaturingBarrelTile
import com.possible_triangle.bigsip.modules.MaturingModule
import com.simibubi.create.api.connectivity.ConnectivityHandler
import com.simibubi.create.content.logistics.block.vault.ItemVaultBlock.HORIZONTAL_AXIS
import com.simibubi.create.foundation.utility.VecHelper
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

class MaturingBarrelItem(block: Block, properties: Properties) : BlockItem(block, properties) {

    override fun place(ctx: BlockPlaceContext): InteractionResult {
        val initialResult = super.place(ctx)
        return if (!initialResult.consumesAction()) {
            initialResult
        } else {
            this.tryMultiPlace(ctx)
            initialResult
        }
    }

    private fun tryMultiPlace(ctx: BlockPlaceContext) {
        val player = ctx.player
        if (player != null) {
            if (!player.isSteppingCarefully) {
                val face = ctx.clickedFace
                val stack = ctx.itemInHand
                val pos = ctx.clickedPos
                val placedOnPos = pos.relative(face.opposite)
                val placedOnState = ctx.level.getBlockState(placedOnPos)
                if (placedOnState.`is`(block)) {
                    val tile = ConnectivityHandler.partAt<MaturingBarrelTile>(MaturingModule.BARREL_TILE,
                        ctx.level,
                        placedOnPos)
                    if (tile != null) {
                        val controller = tile.getControllerTE<MaturingBarrelTile>()
                        if (controller != null) {
                            val width = controller.width
                            if (width != 1) {
                                var tanksToPlace = 0
                                val axis = placedOnState.getValue(HORIZONTAL_AXIS)
                                if (face.axis === axis) {
                                    val facing =
                                        Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE)
                                    val startPos =
                                        if (face == facing.opposite) controller.blockPos.relative(facing.opposite) else controller.blockPos.relative(
                                            facing,
                                            controller.height)
                                    if (VecHelper.getCoordinate(startPos, axis) == VecHelper.getCoordinate(pos, axis)) {
                                        var xOffset: Int
                                        var zOffset: Int
                                        var offsetPos: BlockPos?
                                        var blockState: BlockState
                                        xOffset = 0
                                        while (xOffset < width) {
                                            zOffset = 0
                                            while (zOffset < width) {
                                                offsetPos = if (axis === Direction.Axis.X) startPos.offset(0,
                                                    xOffset,
                                                    zOffset) else startPos.offset(xOffset, zOffset, 0)
                                                blockState = ctx.level.getBlockState(offsetPos)
                                                if (!blockState.`is`(block)) {
                                                    if (!blockState.material.isReplaceable) {
                                                        return
                                                    }
                                                    ++tanksToPlace
                                                }
                                                ++zOffset
                                            }
                                            ++xOffset
                                        }
                                        if (player.isCreative || stack.count >= tanksToPlace) {
                                            xOffset = 0
                                            while (xOffset < width) {
                                                zOffset = 0
                                                while (zOffset < width) {
                                                    offsetPos = if (axis === Direction.Axis.X) startPos.offset(0,
                                                        xOffset,
                                                        zOffset) else startPos.offset(xOffset, zOffset, 0)
                                                    blockState = ctx.level.getBlockState(offsetPos)
                                                    if (!blockState.`is`(block)) {
                                                        val context = BlockPlaceContext.at(ctx, offsetPos, face)
                                                        player.persistentData.putBoolean("SilenceVaultSound", true)
                                                        super.place(context)
                                                        player.persistentData.remove("SilenceVaultSound")
                                                    }
                                                    ++zOffset
                                                }
                                                ++xOffset
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}