package kr.hqservice.framework.netty.pipeline

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import kr.hqservice.framework.netty.packet.AbstractPacket
import kr.hqservice.framework.netty.packet.Direction
import kr.hqservice.framework.netty.packet.extension.writeString
import kotlin.jvm.Throws

class PacketEncoder : MessageToByteEncoder<AbstractPacket>() {

    @Throws(Exception::class)
    override fun encode(ctx: ChannelHandlerContext, packet: AbstractPacket?, out: ByteBuf) {
        if(packet == null)
            throw IllegalArgumentException("packet is null")

        val packetClass = packet::class
        if(Direction.OUTBOUND.findPacketByClass(packetClass) == null)
            throw IllegalArgumentException("packet is not registered to outbound")

        out.writeString(packetClass.qualifiedName
            ?: throw IllegalStateException("'${packetClass}' has not qualified name"))

        out.writeBoolean(packet.isCallbackResult())
        packet.write(out)
    }
}