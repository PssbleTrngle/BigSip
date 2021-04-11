package com.possible_triangle.bigsip.block

import com.possible_triangle.bigsip.Content
import net.minecraft.block.*
import net.minecraft.block.SixWayBlock.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItemUseContext
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.state.StateContainer
import net.minecraft.util.*
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.BlockRayTraceResult
import net.minecraft.util.math.shapes.ISelectionContext
import net.minecraft.util.math.shapes.VoxelShape
import net.minecraft.world.IBlockReader
import net.minecraft.world.IWorld
import net.minecraft.world.World

class GrapeCrop : CropsBlock(Properties.copy(Blocks.CARROTS)) {

    companion object {
        val PROPERTY_BY_DIRECTION = SixWayBlock.PROPERTY_BY_DIRECTION.filter { it.key.axis.isHorizontal }
        private val SHAPE = box(2.0, 0.0, 2.0, 14.0, 24.0, 14.0)
    }

    override fun createBlockStateDefinition(builder: StateContainer.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(NORTH, EAST, SOUTH, WEST)
    }

    override fun getCloneItemStack(world: IBlockReader, pos: BlockPos, state: BlockState): ItemStack {
        return ItemStack(Content.GRAPE_SAPLING)
    }

    override fun use(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockRayTraceResult): ActionResultType {
        val age = state.getValue(AGE)
        val fullyGrown = age == 7
        return if (!fullyGrown && player.getItemInHand(hand).item === Items.BONE_MEAL) {
            ActionResultType.PASS
        } else if (age >= 5) {
            val j = 1 + world.random.nextInt(2)
            popResource(world, pos, ItemStack(Content.GRAPES, j + (age - 4)))
            world.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0f, 0.8f + world.random.nextFloat() * 0.4f)
            world.setBlock(pos, state.setValue(AGE, 4), 2)
            ActionResultType.sidedSuccess(world.isClientSide)
        } else {
            super.use(state, world, pos, player, hand, hit)
        }
    }

    override fun getStateForPlacement(ctx: BlockItemUseContext): BlockState {
        return PROPERTY_BY_DIRECTION.map { (direction, prop) -> prop to ctx.clickedPos.relative(direction) }
            .map { (key, pos) -> key to ctx.level.getBlockState(pos) }
            .map { (key, block) -> key to block.`is`(Content.GRAPE_CROP) }
            .fold(super.getStateForPlacement(ctx)!!, { state, (prop, value) ->
                state.setValue(prop, value)
            })
    }

    override fun updateShape(state: BlockState, direction: Direction, neighbour: BlockState, world: IWorld, pos: BlockPos, neighbourPos: BlockPos): BlockState {
        val superState = super.updateShape(state, direction, neighbour, world, pos, neighbourPos)
        return if (superState.`is`(Content.GRAPE_CROP) && direction.axis.plane == Direction.Plane.HORIZONTAL) superState.setValue(
            PROPERTY_BY_DIRECTION[direction],
            neighbour.`is`(Content.GRAPE_CROP)
        ) else superState
    }

    override fun getShape(state: BlockState, world: IBlockReader, pos: BlockPos, ctx: ISelectionContext): VoxelShape {
        return SHAPE
    }

}