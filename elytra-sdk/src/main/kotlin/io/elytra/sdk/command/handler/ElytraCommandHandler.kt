package io.elytra.sdk.command.handler

import io.elytra.api.command.Command
import io.elytra.api.command.argument.Argument
import io.elytra.api.command.handler.CommandHandler
import io.elytra.api.command.registry.CommandRegistry
import io.elytra.api.entity.Player
import io.elytra.sdk.server.Elytra
import io.elytra.sdk.utils.ElytraConsts

class ElytraCommandHandler(val commandRegistry: CommandRegistry) : CommandHandler {

	override fun handle(player: Player, string: String) {
		if (!string.startsWith(ElytraConsts.COMMAND_PREFIX)) return

		val stringWithoutPrefix = string.substring(1)
		val split = stringWithoutPrefix.split(" ")

		if (split.isEmpty()) return

		val commandName = split[0]

		val command: Command? = commandRegistry.getCommandByName(commandName)

		if (command == null) {
			player.sendMessage(ElytraConsts.COMMAND_NOT_FOUND_MESSAGE)
			Elytra.console.debug("Command not found $commandName")
			return
		}

		val argumentList: MutableList<Argument<*>> = ArrayList()
		val stringArgumentList = split.subList(1, split.size)

		for ((index, argumentContext) in command.arguments.withIndex()) {
			if (split.size <= index) {
				player.sendMessage("Wrong usage")
				TODO("Implement command usage message")
			}

			// TODO: Implement ArgumentContext required boolean

			val argument = argumentContext.type.parse(stringArgumentList, index)
				?: TODO("Send message couldn't parse string")

			argumentList.add(argument as Argument<*>)
		}

		command.execute(player, argumentList)
		Elytra.console.debug("Executed command $command")
	}

}
