package io.elytra.sdk.events

import io.elytra.api.events.ElytraEvent
import io.elytra.api.server.Motd

data class ServerListPingEvent(
    val motd: Motd
) : ElytraEvent
