package com.possible_triangle.bigsip.config

import com.possible_triangle.bigsip.BigSip
import com.possible_triangle.bigsip.network.Networking
import com.possible_triangle.bigsip.network.SyncConfigMessage
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.loading.FMLPaths
import net.minecraftforge.fml.server.ServerLifecycleHooks
import java.io.IOException
import java.nio.file.Files

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object Configs {

    var SERVER_SPEC: ForgeConfigSpec
        private set
    var SERVER: ServerConfig
        private set

    init {
        with(ForgeConfigSpec.Builder().configure { ServerConfig(it) }) {
            SERVER = left
            SERVER_SPEC = right
        }
    }

    @SubscribeEvent
    fun configReload(event: ModConfig.Reloading) {
        if (event.config.type == ModConfig.Type.COMMON) {
            ServerLifecycleHooks.getCurrentServer().playerList.players.forEach {
                syncServerConfigs(it)
            }
        }
    }

    private fun syncServerConfigs(player: ServerPlayerEntity) {
        val config = FMLPaths.CONFIGDIR.get().resolve(BigSip.MOD_ID + "-common.toml").toAbsolutePath()
        try {
            val configData: ByteArray = Files.readAllBytes(config)
            Networking.sendTo(SyncConfigMessage(configData), player)
        } catch (ignored: IOException) {
            BigSip.LOGGER.warn("Could not sync config file to player")
        }
    }

}