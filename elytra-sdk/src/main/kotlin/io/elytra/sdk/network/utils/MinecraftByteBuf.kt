package io.elytra.sdk.network.utils

import com.flowpowered.network.util.ByteBufUtils
import io.elytra.api.chat.TextComponent
import io.elytra.api.world.Position
import io.elytra.sdk.utils.asJson
import io.elytra.sdk.utils.fromJson
import io.netty.buffer.ByteBuf
import io.netty.handler.codec.DecoderException
import io.netty.handler.codec.EncoderException
import java.nio.charset.StandardCharsets
import java.util.*

val ByteBuf.minecraft get() = MinecraftByteBuf(this)

inline class MinecraftByteBuf(private val byteBuf: ByteBuf) {

    fun writeEnumValue(value: Enum<*>) {
        ByteBufUtils.writeVarInt(byteBuf, value.ordinal)
    }

    // Formula from https://wiki.vg/Data_types
    fun readPosition(): Position {
        val positionFromBuffer: Long = byteBuf.readLong()

        val x = positionFromBuffer shr 38
        val y = positionFromBuffer and 0xFFF
        val z = positionFromBuffer shl 26 shr 38

        return Position(x.toDouble(), y.toDouble(), z.toDouble())
    }

    // Formula from https://wiki.vg/Data_types
    fun writePosition(position: Position) {
        val x = position.x.toInt()
        val y = position.y.toInt()
        val z = position.z.toInt()

        byteBuf.writeLong((x and 0x3ffffff shl 38 or (z and 0x3FFFFFF shl 12) or (y and 0xFFF)).toLong())
    }

    fun <T : Enum<T>?> readEnumValue(enumClass: Class<T>): T {
        return (enumClass.enumConstants as Array<*>)[ByteBufUtils.readVarInt(byteBuf)] as T
    }

    fun writeUuid(uuid: UUID) {
        byteBuf.writeLong(uuid.mostSignificantBits)
        byteBuf.writeLong(uuid.leastSignificantBits)
    }

    fun readUuid(): UUID {
        return UUID(byteBuf.readLong(), byteBuf.readLong())
    }

    fun writeTextComponent(textComponent: TextComponent) {
        writeString(textComponent.asJson())
    }

    // TODO Verify this
    fun readTextComponent(): TextComponent {
        return fromJson(readString(256))
    }

    fun writeString(string: String): ByteBuf? {
        val bytes = string.toByteArray(StandardCharsets.UTF_8)

        return if (bytes.size > 32767) {
            throw EncoderException("String too big (was " + bytes.size + " bytes encoded, max " + 32767 + ")")
        } else {
            ByteBufUtils.writeVarInt(byteBuf, bytes.size)
            byteBuf.writeBytes(bytes)
        }
    }

    fun readString(maxLength: Int): String {
        val i: Int = ByteBufUtils.readVarInt(byteBuf)
        return if (i > maxLength * 4) {
            throw DecoderException("The received encoded string buffer length is longer than maximum allowed (" + i + " > " + maxLength * 4 + ")")
        } else if (i < 0) {
            throw DecoderException("The received encoded string buffer length is less than zero! Weird string!")
        } else {
            val stringBuffer = byteBuf.toString(byteBuf.readerIndex(), i, StandardCharsets.UTF_8)
            byteBuf.readerIndex(byteBuf.readerIndex() + i)
            if (stringBuffer.length > maxLength) {
                throw DecoderException("The received string length is longer than maximum allowed ($i > $maxLength)")
            } else {
                stringBuffer
            }
        }
    }

    fun writeByteArray(array: ByteArray) {
        ByteBufUtils.writeVarInt(byteBuf, array.size)
        byteBuf.writeBytes(array)
    }

    fun readByteArray(): ByteArray? {
        return this.readByteArray(byteBuf.readableBytes())
    }

    fun readByteArray(maxLength: Int): ByteArray? {
        val i: Int = ByteBufUtils.readVarInt(byteBuf)
        return if (i > maxLength) {
            throw DecoderException("ByteArray with size $i is bigger than allowed $maxLength")
        } else {
            val abyte = ByteArray(i)
            byteBuf.readBytes(abyte)
            abyte
        }
    }
}
