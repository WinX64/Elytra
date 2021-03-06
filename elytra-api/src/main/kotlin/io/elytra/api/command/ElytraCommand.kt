package io.elytra.api.command

import io.elytra.api.command.annotations.CommandArgument

/**
 * Representation of a server command
 *
 * All commands must extends this in order to be functional
 */
abstract class ElytraCommand : Command {
    override var label: String = ""
    override val aliases: MutableList<String> = ArrayList()
    override val argumentList: MutableList<CommandArgument> = ArrayList()
}
