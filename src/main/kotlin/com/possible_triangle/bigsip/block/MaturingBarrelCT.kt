package com.possible_triangle.bigsip.block

import com.possible_triangle.bigsip.Content
import com.possible_triangle.bigsip.block.tile.MaturingBarrelTile
import com.simibubi.create.api.connectivity.ConnectivityHandler
import com.simibubi.create.content.logistics.block.vault.ItemVaultBlock.HORIZONTAL_AXIS
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.state.BlockState

class MaturingBarrelCT(private val isBarrel: (BlockState) -> Boolean) : ConnectedTextureBehaviour.Base() {

    override fun getShift(state: BlockState, direction: Direction, atlas: TextureAtlasSprite?): CTSpriteShiftEntry? {
        if (!isBarrel(state)) return null
        val axis = state.getValue(HORIZONTAL_AXIS)
        //val small = !state.getValue(LARGE)

        return if (direction.axis === axis) {
            Content.BARREL_CT_FRONT
        } else if (direction == Direction.UP) {
            Content.BARREL_CT_TOP
        } else if (direction == Direction.DOWN) {
            Content.BARREL_CT_BOTTOM
        } else {
            Content.BARREL_CT_SIDE
        }
    }

    override fun getUpDirection(
        reader: BlockAndTintGetter,
        pos: BlockPos,
        state: BlockState,
        face: Direction,
    ): Direction {
        val axis = state.getValue(HORIZONTAL_AXIS)
        val alongX = axis === Direction.Axis.X

        return if (face.axis.isVertical && alongX) {
            super.getUpDirection(reader, pos, state, face).clockWise
        } else {
            if (face.axis !== axis && !face.axis.isVertical) Direction.fromAxisAndDirection(axis,
                if (alongX) Direction.AxisDirection.POSITIVE else Direction.AxisDirection.NEGATIVE) else super.getUpDirection(
                reader,
                pos,
                state,
                face)
        }
    }

    override fun getRightDirection(
        reader: BlockAndTintGetter,
        pos: BlockPos,
        state: BlockState,
        face: Direction,
    ): Direction {
        val axis = state.getValue(HORIZONTAL_AXIS)

        return if (face.axis.isVertical && axis === Direction.Axis.X) {
            super.getRightDirection(reader, pos, state, face).clockWise
        } else if (face.axis !== axis && !face.axis.isVertical) {
            Direction.fromAxisAndDirection(Direction.Axis.Y, face.axisDirection)
        } else {
            super.getRightDirection(reader, pos, state, face)
        }
    }

    override fun connectsTo(
        state: BlockState,
        other: BlockState,
        reader: BlockAndTintGetter,
        pos: BlockPos,
        otherPos: BlockPos,
        face: Direction,
    ): Boolean {
        return state === other && ConnectivityHandler.isConnected<MaturingBarrelTile>(reader, pos, otherPos)
    }

}