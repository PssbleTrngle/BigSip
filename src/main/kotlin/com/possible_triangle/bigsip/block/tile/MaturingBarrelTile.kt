package com.possible_triangle.bigsip.block.tile

//import com.simibubi.create.content.logistics.block.vault.ItemVaultBlock.LARGE
import com.possible_triangle.bigsip.modules.MaturingModule
import com.simibubi.create.api.connectivity.ConnectivityHandler
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation
import com.simibubi.create.foundation.fluid.SmartFluidTank
import com.simibubi.create.foundation.tileEntity.IMultiTileContainer
import com.simibubi.create.foundation.tileEntity.SmartTileEntity
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour
import com.simibubi.create.foundation.utility.Lang
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction
import net.minecraftforge.fluids.capability.templates.FluidTank
import kotlin.math.ceil

class MaturingBarrelTile(pos: BlockPos, state: BlockState) : SmartTileEntity(MaturingModule.BARREL_TILE, pos, state),
    IMultiTileContainer.Fluid, IHaveGoggleInformation {

    companion object {
        private const val LITERS = 8000
    }

    private val tank = SmartFluidTank(LITERS, ::onFluidChanged)
    private var fluidCapability = LazyOptional.of<FluidTank> { tank }
    private var controllerPos: BlockPos? = null
    private var updateConnectivity = false
    private var radius = 1
    private var length = 1
    private var lastPos: BlockPos? = null
    private var syncCooldown = 0
    private var queuedSync = false
    private var forceFluidLevelUpdate = false
    private val actor = object : MaturingActor() {
        override fun getFluid() = tank.fluid
        override fun setFluid(value: FluidStack) {
            tank.fluid = value
        }

        override fun onChange() {
            setChanged()
            sendData()
        }
    }

    init {
        refreshCapability()
    }

    private fun onFluidChanged(fluid: FluidStack) {
        if (isController) actor.onFluidChanged(fluid)
        if (hasLevel() && !level!!.isClientSide) {
            setChanged()
            sendData()
        }
    }

    override fun tick() {
        super.tick()

        if (syncCooldown > 0) {
            --syncCooldown
            if (syncCooldown == 0 && queuedSync) {
                sendData()
            }
        }

        if (lastPos == null) {
            lastPos = blockPos
        } else if (lastPos != worldPosition && worldPosition != null) {
            removeController(true)
            lastPos = worldPosition
            return
        }

        if (updateConnectivity) updateConnectivity()

        if (isController) actor.tick(level ?: return)

    }

    override fun read(compound: CompoundTag, clientPacket: Boolean) {
        super.read(compound, clientPacket)
        val controllerBefore = controllerPos
        val prevSize = width
        val prevHeight = height
        updateConnectivity = compound.contains("Uninitialized")
        controllerPos = null
        lastPos = null

        if (compound.contains("LastKnownPos")) {
            lastPos = NbtUtils.readBlockPos(compound.getCompound("LastKnownPos"))
        }
        if (compound.contains("Controller")) {
            controllerPos = NbtUtils.readBlockPos(compound.getCompound("Controller"))
        }

        actor.read(compound, clientPacket)

        if (isController) {
            radius = compound.getInt("Size")
            length = compound.getInt("Length")
            tank.capacity = totalTankSize * LITERS
            tank.readFromNBT(compound.getCompound("TankContent"))
            if (tank.space < 0) {
                tank.drain(-tank.space, FluidAction.EXECUTE)
            }
        }

        if (clientPacket) {
            val changeOfController =
                if (controllerBefore == null) controllerPos != null else controllerBefore != controllerPos
            if (changeOfController || prevSize != width || prevHeight != height) {
                if (hasLevel()) {
                    level!!.sendBlockUpdated(this.blockPos, blockState, blockState, 16)
                }
                if (this.isController) {
                    this.tank.capacity = LITERS * totalTankSize
                }
                invalidateRenderBoundingBox()
            }
        }
    }

    override fun write(compound: CompoundTag, clientPacket: Boolean) {
        if (updateConnectivity) {
            compound.putBoolean("Uninitialized", true)
        }

        if (lastPos != null) {
            compound.put("LastKnownPos", NbtUtils.writeBlockPos(lastPos!!))
        }

        actor.write(compound, clientPacket)

        if (isController) {
            compound.put("TankContent", tank.writeToNBT(CompoundTag()))
            compound.putInt("Size", radius)
            compound.putInt("Length", length)
        } else {
            compound.put("Controller", NbtUtils.writeBlockPos(controllerPos!!))
        }

        super.write(compound, clientPacket)

        if (clientPacket) {
            if (queuedSync) compound.putBoolean("LazySync", true)
            if (forceFluidLevelUpdate) {
                compound.putBoolean("ForceFluidLevel", true)
                forceFluidLevelUpdate = false
            }
        }
    }

    private val totalTankSize
        get() = this.width * this.width * this.height

    internal fun updateConnectivity() {
        updateConnectivity = false
        if (!level!!.isClientSide && this.isController) {
            ConnectivityHandler.formMulti(this)
        }
    }

    override fun addToGoggleTooltip(tooltip: List<Component>, isPlayerSneaking: Boolean): Boolean {
        val controllerTE = controllerTile
        return if (controllerTE == null) {
            false
        } else {
            val fluidCapability = controllerTE.getCapability(FLUID_HANDLER_CAPABILITY)
            val hasFluid = this.containedFluidTooltip(tooltip, isPlayerSneaking, fluidCapability)

            val progress = controllerTE.actor.progressPercentage

            if (tooltip is MutableList) {
                if (progress > 0) {
                    val percentage = (progress * 100F).toInt()
                    val barWidth = 40
                    val stripes = ceil(barWidth * progress).toInt()
                    Lang.builder()
                        .text(" ")
                        .text("|".repeat(stripes))
                        .add(
                            Lang.builder()
                                .text("|".repeat(barWidth - stripes))
                                .style(ChatFormatting.DARK_GRAY)
                        )
                        .text(" $percentage%")
                        .forGoggles(tooltip)
                }

            }

            hasFluid || progress > 0
        }
    }

    private fun refreshCapability() {
        val oldFluidCapability = fluidCapability

        fluidCapability = LazyOptional.of {
            if (isController) tank
            else controllerTile?.tank ?: FluidTank(0)
        }
        oldFluidCapability.invalidate()
    }

    private val controllerTile get() = getControllerTE<MaturingBarrelTile>()
    override fun <T> getControllerTE(): T? where T : BlockEntity, T : IMultiTileContainer {
        val tile = if (this.isController) {
            this
        } else {
            val tile = level!!.getBlockEntity(this.controllerPos!!)
            if (tile is MaturingBarrelTile) tile
            else null
        }
        return tile as T?
    }

    override fun isController(): Boolean {
        return controllerPos == null || worldPosition == controllerPos
    }

    private fun invalidate() {
        fluidCapability.invalidate()
        setChanged()
        sendData()
    }

    override fun initialize() {
        super.initialize()
        sendData()
    }

    override fun setController(pos: BlockPos?) {
        if (level!!.isClientSide() && !isVirtual) return
        if (pos != controllerPos) {
            controllerPos = pos
            invalidate()
            refreshCapability()
        }
    }

    override fun createRenderBoundingBox(): AABB? {
        return if (this.isController) super.createRenderBoundingBox()
            .expandTowards((this.width - 1).toDouble(), (height - 1).toDouble(), (width - 1).toDouble())
        else super.createRenderBoundingBox()
    }

    override fun removeController(keepFluids: Boolean) {
        if (level!!.isClientSide()) return
        if (!keepFluids) applyFluidTankSize(1)
        this.updateConnectivity = true
        this.controllerPos = null
        this.radius = 1
        this.length = 1

        this.onFluidChanged(tank.fluid)

        //if (blockState.`is`(Content.BARREL)) {
        //    blockState.setValue(LARGE, false).also {
        //        getLevel()!!.setBlock(worldPosition, it, 22)
        //    }
        //}

        invalidate()
    }

    override fun getLastKnownPos() = lastPos

    override fun preventConnectivityUpdate() {
        updateConnectivity = false
    }

    override fun notifyMultiUpdated() {
        //if (blockState.`is`(Content.BARREL)) {
        //    level!!.setBlock(this.blockPos, blockState.setValue(LARGE, radius > 2) as BlockState, 6)
        //}

        fluidCapability.invalidate()
        setChanged()
    }

    override fun getMainConnectionAxis(): Direction.Axis {
        return getMainAxisOf(this)
    }

    override fun getMaxLength(axis: Direction.Axis, length: Int): Int {
        return if (axis == Direction.Axis.Y) maxWidth
        else 1 + length
    }

    override fun getMaxWidth(): Int = 2

    override fun getHeight(): Int = length

    override fun setHeight(value: Int) {
        length = value
    }

    override fun getWidth(): Int = radius

    override fun setWidth(value: Int) {
        radius = value
    }

    override fun hasTank(): Boolean {
        return true
    }

    override fun getTank(tank: Int) = this.tank

    override fun getTankSize(tank: Int) = LITERS

    override fun getController(): BlockPos? {
        return if (this.isController) worldPosition else controllerPos
    }

    override fun addBehaviours(behaviours: MutableList<TileEntityBehaviour>) {
        // No behaviours here
    }

    override fun <T> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        if (!fluidCapability.isPresent) refreshCapability()
        return if (cap == FLUID_HANDLER_CAPABILITY) fluidCapability.cast()
        else super.getCapability(cap, side)
    }

    private fun applyFluidTankSize(blocks: Int) {
        this.tank.capacity = blocks * LITERS
        val overflow: Int = tank.fluidAmount - tank.capacity
        if (overflow > 0) tank.drain(overflow, FluidAction.EXECUTE)
        this.forceFluidLevelUpdate = true
    }

    override fun setTankSize(tank: Int, blocks: Int) {
        this.applyFluidTankSize(blocks)
    }
}