package com.possible_triangle.bigsip.network

import com.electronwill.nightconfig.toml.TomlFormat
import com.possible_triangle.bigsip.config.Configs
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.network.NetworkDirection
import net.minecraftforge.fml.network.NetworkEvent
import java.io.ByteArrayInputStream
import java.util.function.Supplier


class SyncConfigMessage(private val configData: ByteArray) {

    companion object {
        fun encode(message: SyncConfigMessage, buf: PacketBuffer) {
            buf.writeByteArray(message.configData)
        }

        fun decode(buf: PacketBuffer): SyncConfigMessage {
            return SyncConfigMessage(buf.readByteArray())
        }

        fun handle(message: SyncConfigMessage, context: Supplier<NetworkEvent.Context>) {
            with(context.get()) {
                enqueueWork {
                    if (direction == NetworkDirection.PLAY_TO_CLIENT) {
                        Configs.SERVER_SPEC.setConfig(
                            TomlFormat.instance().createParser().parse(ByteArrayInputStream(message.configData))
                        )
                    }
                }
                packetHandled = true
            }
        }
    }

}