package com.possible_triangle.bigsip.block

import com.possible_triangle.bigsip.modules.GrapesModule
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.CropBlock
import net.minecraft.world.level.block.PipeBlock
import net.minecraft.world.level.block.PipeBlock.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.*
import kotlin.math.min

open class GrapeCrop : CropBlock(Properties.copy(Blocks.CARROTS)) {

    init {
        registerDefaultState(PROPERTY_BY_DIRECTION.values.fold(defaultBlockState()) { state, prop ->
            state.setValue(prop, false)
        })
    }

    companion object {
        val PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION.filter { it.key.axis.isHorizontal }
        private val SHAPE = box(2.0, 0.0, 2.0, 14.0, 24.0, 14.0)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(NORTH, EAST, SOUTH, WEST)
    }

    override fun getCloneItemStack(
        state: BlockState?,
        target: HitResult?,
        level: BlockGetter?,
        pos: BlockPos?,
        player: Player?,
    ): ItemStack {
        return ItemStack(GrapesModule.GRAPE_SAPLING)
    }

    override fun use(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult,
    ): InteractionResult {
        val age = state.getValue(AGE)
        val fullyGrown = age == 7
        return if (!fullyGrown && player.getItemInHand(hand).item === Items.BONE_MEAL) {
            InteractionResult.PASS
        } else if (age >= 5) {
            val j = 1 + world.random.nextInt(2)
            popResource(world, pos, ItemStack(GrapesModule.GRAPES, j + (age - 4)))
            world.playSound(
                null,
                pos,
                SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES,
                SoundSource.BLOCKS,
                1.0f,
                0.8f + world.random.nextFloat() * 0.4f
            )
            world.setBlock(pos, state.setValue(AGE, 4), 2)
            InteractionResult.sidedSuccess(world.isClientSide)
        } else {
            super.use(state, world, pos, player, hand, hit)
        }
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        return updateConnections(ctx.level, super.getStateForPlacement(ctx)!!, ctx.clickedPos)
    }

    private fun updateConnections(world: Level, state: BlockState, pos: BlockPos): BlockState {
        return PROPERTY_BY_DIRECTION
            .map { (direction, prop) ->
                prop to shouldConnect(
                    state,
                    world.getBlockState(pos.relative(direction)),
                    direction
                )
            }
            .fold(state) { it, (prop, value) ->
                it.setValue(prop, value)
            }
    }

    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighbour: BlockState,
        world: LevelAccessor,
        pos: BlockPos,
        neighbourPos: BlockPos,
    ): BlockState {
        val superState = super.updateShape(state, direction, neighbour, world, pos, neighbourPos)
        return if (superState.`is`(GrapesModule.GRAPE_CROP) && direction.axis.plane == Direction.Plane.HORIZONTAL) superState.setValue(
            PROPERTY_BY_DIRECTION[direction]!!,
            shouldConnect(superState, neighbour, direction)
        ) else superState
    }

    private fun shouldConnect(state: BlockState, neighbour: BlockState, direction: Direction): Boolean {
        if(!neighbour.`is`(GrapesModule.GRAPE_CROP)) return false
        val neighbourAge = neighbour.getValue(ageProperty)
        val selfAge = state.getValue(ageProperty)
        return if(neighbourAge == selfAge) direction == Direction.EAST || direction == Direction.NORTH
        else neighbourAge > selfAge
    }

    override fun getShape(state: BlockState, world: BlockGetter, pos: BlockPos, ctx: CollisionContext): VoxelShape {
        return SHAPE
    }

    override fun performBonemeal(world: ServerLevel, random: Random, pos: BlockPos, state: BlockState) {
        val nextAge = min(maxAge, getAge(state) + getBonemealAgeIncrease(world))
        world.setBlock(pos, updateConnections(world, state.setValue(ageProperty, nextAge), pos), 2)
    }

}