package com.possible_triangle.bigsip.network

import com.possible_triangle.bigsip.BigSip
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.network.NetworkRegistry
import net.minecraftforge.fml.network.PacketDistributor


object Networking {

    private const val version = "1.0"

    private val channel = NetworkRegistry.newSimpleChannel(ResourceLocation(BigSip.MOD_ID, "network"),
        { version },
        { version == it },
        { version == it }
    )

    init {
        channel.registerMessage(
            1,
            SyncConfigMessage::class.java,
            SyncConfigMessage::encode,
            SyncConfigMessage::decode,
            SyncConfigMessage::handle
        )
    }

    fun sendTo(message: Any, player: ServerPlayerEntity) {
        channel.send(PacketDistributor.PLAYER.with { player }, message)
    }

}