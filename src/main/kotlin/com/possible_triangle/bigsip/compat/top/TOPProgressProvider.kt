package com.possible_triangle.bigsip.compat.top

import com.possible_triangle.bigsip.BigSip
import com.possible_triangle.bigsip.block.tile.MaturingBarrelTile
import mcjty.theoneprobe.api.*
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

object TOPProgressProvider : IProbeInfoProvider {

    override fun getID() = ResourceLocation(BigSip.MOD_ID, "progress")

    override fun addProbeInfo(
        mode: ProbeMode,
        info: IProbeInfo,
        player: Player,
        level: Level,
        state: BlockState,
        data: IProbeHitData,
    ) {
        val tile = level.getBlockEntity(data.pos)
        if(mode == ProbeMode.NORMAL) return

        if (tile is MaturingBarrelTile) {
            val controller = tile.getControllerTE<MaturingBarrelTile>() ?: return
            val progress = controller.progress?.progressPercentage ?: return
            if(progress <= 0F) return

            val style = ProgressStyle().apply {
                suffix("%")
                alternateFilledColor(Color(filledColor).darker())
            }

            info.progress((progress * 100).toInt(), 100, style)
        }
    }
}