package io.elytra.sdk.network.protocol.codecs

import com.flowpowered.network.Codec
import io.elytra.sdk.network.protocol.message.play.outbound.DisconnectMessage
import io.elytra.sdk.network.utils.minecraft
import io.elytra.sdk.utils.asJson
import io.netty.buffer.ByteBuf

class DisconnectCodec : Codec<DisconnectMessage> {
    override fun encode(buffer: ByteBuf, message: DisconnectMessage): ByteBuf {
        buffer.minecraft.writeString(message.textComponent.asJson())
        return buffer
    }

    override fun decode(buffer: ByteBuf): DisconnectMessage {
        return DisconnectMessage(buffer.minecraft.readString(32767))
    }
}
