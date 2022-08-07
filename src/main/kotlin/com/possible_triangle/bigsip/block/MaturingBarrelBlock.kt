package com.possible_triangle.bigsip.block

import com.possible_triangle.bigsip.block.tile.MaturingBarrelTile
import com.possible_triangle.bigsip.modules.MaturingModule
import com.simibubi.create.api.connectivity.ConnectivityHandler
import com.simibubi.create.content.contraptions.wrench.IWrenchable
import com.simibubi.create.content.logistics.block.vault.ItemVaultBlock.HORIZONTAL_AXIS
//import com.simibubi.create.content.logistics.block.vault.ItemVaultBlock.LARGE
import com.simibubi.create.foundation.block.ITE
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition

class MaturingBarrelBlock : Block(Properties.copy(Blocks.BARREL)), ITE<MaturingBarrelTile>, IWrenchable {

    init {
        //this.registerDefaultState(this.defaultBlockState().setValue(LARGE, false))
    }

    override fun getTileEntityClass(): Class<MaturingBarrelTile> {
        return MaturingBarrelTile::class.java
    }

    override fun getTileEntityType(): BlockEntityType<out MaturingBarrelTile> {
        return MaturingModule.BARREL_TILE
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        //builder.add(HORIZONTAL_AXIS, LARGE)
        builder.add(HORIZONTAL_AXIS)
        super.createBlockStateDefinition(builder)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        if (context.player == null || !context.player!!.isSteppingCarefully) {
            val placedOn = context.level.getBlockState(context.clickedPos.relative(context.clickedFace.opposite))
            if (placedOn.`is`(MaturingModule.BARREL)) {
                val axis = placedOn.getValue(HORIZONTAL_AXIS)
                return defaultBlockState().setValue(HORIZONTAL_AXIS, axis) as BlockState
            }
        }
        return defaultBlockState().setValue(HORIZONTAL_AXIS, context.horizontalDirection.axis) as BlockState
    }

    override fun onPlace(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pOldState: BlockState,
        moving: Boolean,
    ) {
        if (pOldState.block !== pState.block && !moving) {
            withTileEntityDo(pLevel, pPos) { it.updateConnectivity() }
        }
    }

    override fun onWrenched(state: BlockState, context: UseOnContext): InteractionResult? {
        val updatedState = if (context.clickedFace.axis.isVertical) {
            val tile = context.level.getBlockEntity(context.clickedPos)
            if (tile is MaturingBarrelTile) {
                ConnectivityHandler.splitMulti(tile)
                tile.removeController(true)
            }
            state
            //state.setValue(LARGE, false) as BlockState
        } else state
        return super.onWrenched(updatedState, context)
    }

    override fun onRemove(state: BlockState, world: Level, pos: BlockPos, newState: BlockState, pIsMoving: Boolean) {
        if (state.hasBlockEntity() && (state.block !== newState.block || !newState.hasBlockEntity())) {
            val tile = world.getBlockEntity(pos) as? MaturingBarrelTile ?: return
            world.removeBlockEntity(pos)
            ConnectivityHandler.splitMulti(tile)
        }
    }

    override fun rotate(state: BlockState, rot: Rotation): BlockState {
        val axis = state.getValue(HORIZONTAL_AXIS) as Direction.Axis
        return state.setValue(HORIZONTAL_AXIS,
            rot.rotate(Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE)).axis) as BlockState
    }

    override fun mirror(state: BlockState, mirror: Mirror): BlockState = state


}