package io.elytra.sdk.network.protocol.handlers.play

import com.flowpowered.network.MessageHandler
import io.elytra.api.chat.ChatMode
import io.elytra.api.chat.TextComponent
import io.elytra.sdk.network.NetworkSession
import io.elytra.sdk.network.protocol.message.play.inbound.ChatMessage
import io.elytra.sdk.network.protocol.message.play.outbound.OutboundChatMessage
import io.elytra.sdk.server.Elytra
import io.elytra.sdk.utils.ElytraConsts

class ChatHandler : MessageHandler<NetworkSession, ChatMessage> {

    override fun handle(session: NetworkSession, message: ChatMessage) {
        if (message.content.startsWith(ElytraConsts.COMMAND_PREFIX)) {
            val player = Elytra.server.playerRegistry.get(session)
            Elytra.server.commandHandler.handle(player!!, message.content)
            return
        }

        val content = message.content.replace('&', '\u00A7')
        val textComponent = TextComponent("${session.gameProfile?.name}: $content")
        Elytra.sendPacketToAll(OutboundChatMessage(textComponent, ChatMode.PLAYER))
    }
}
