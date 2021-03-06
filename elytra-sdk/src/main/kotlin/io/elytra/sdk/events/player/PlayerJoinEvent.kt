package io.elytra.sdk.events.player

import io.elytra.api.entity.Player
import io.elytra.api.events.ElytraEvent

class PlayerJoinEvent(val player: Player) : ElytraEvent
