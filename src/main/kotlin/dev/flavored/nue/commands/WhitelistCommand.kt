package dev.flavored.nue.commands

import dev.flavored.nue.utils.mm
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.CommandExecutor

class WhitelistCommand : Command("whitelist") {
    init {
        defaultExecutor = CommandExecutor { sender, _ ->
            sender.sendMessage("<red>Usage: /whitelist <add/remove/list>".mm())
        }
        addSubcommand(AddSubcommand())
        addSubcommand(RemoveSubcommand())
        addSubcommand(ListSubcommand())
        // TODO: Add permission check
    }

    private class AddSubcommand : Command("add"), CommandExecutor {
        init {
            defaultExecutor = this
        }

        override fun apply(sender: CommandSender, context: CommandContext) {

        }
    }

    private class RemoveSubcommand : Command("remove"), CommandExecutor {
        init {
            defaultExecutor = this
        }

        override fun apply(sender: CommandSender, context: CommandContext) {

        }
    }

    private class ListSubcommand : Command("list"), CommandExecutor {
        init {
            defaultExecutor = this
        }

        override fun apply(sender: CommandSender, context: CommandContext) {

        }
    }
}