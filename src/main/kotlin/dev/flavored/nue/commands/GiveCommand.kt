package dev.flavored.nue.commands

import dev.flavored.nue.utils.mm
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.CommandExecutor
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Player

class GiveCommand : Command("give") {
    private val targets = ArgumentType.Entity("targets")
        .onlyPlayers(true)
    private val item = ArgumentType.ItemStack("item")
    private val count = ArgumentType.Integer("count")

    init {
        defaultExecutor = CommandExecutor { sender, _ ->
            sender.sendMessage("<red>Usage: /give <target> <item> [<count>]".mm())
        }
        addSyntax(this::applyWithTargetsAndItem, targets, item)
        addSyntax(this::applyWithTargetsAndItemAndCount, targets, item, count)
    }

    private fun applyWithTargetsAndItem(sender: CommandSender, context: CommandContext) {
        val item = context.get(item)
        context.get(targets).find(sender).forEach { target ->
            (target as Player).inventory.addItemStack(item)
        }
    }

    private fun applyWithTargetsAndItemAndCount(sender: CommandSender, context: CommandContext) {
        val count = context.get(count)
        val item = context.get(item).withAmount(count)
        context.get(targets).find(sender).forEach { target ->
            (target as Player).inventory.addItemStack(item)
        }
    }
}