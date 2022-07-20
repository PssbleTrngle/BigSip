package com.possible_triangle.bigsip.network

import com.electronwill.nightconfig.toml.TomlFormat
import com.possible_triangle.bigsip.config.Configs
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkEvent
import java.io.ByteArrayInputStream
import java.util.function.Supplier


class SyncConfigMessage(private val configData: ByteArray) {

    companion object {
        fun encode(message: SyncConfigMessage, buf: FriendlyByteBuf) {
            buf.writeByteArray(message.configData)
        }

        fun decode(buf: FriendlyByteBuf): SyncConfigMessage {
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