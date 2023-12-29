package dev.flavored.nue.commands

import dev.flavored.nue.utils.mm
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.CommandExecutor
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Player
import net.minestom.server.inventory.TransactionOption
import net.minestom.server.inventory.TransactionType

class ClearCommand : Command("clear"), CommandExecutor {
    private val targets = ArgumentType.Entity("targets")
        .onlyPlayers(true)
    private val item = ArgumentType.ItemStack("item")
    private val maxCount = ArgumentType.Integer("maxCount")

    init {
        defaultExecutor = this
        addSyntax(this::applyWithTargets, targets)
        addSyntax(this::applyWithTargetsAndItem, targets, item)
        addSyntax(this::applyWithTargetsAndItemAndMaxCount, targets, item, maxCount)
    }

    override fun apply(sender: CommandSender, context: CommandContext) {
        if (sender !is Player) {
            sender.sendMessage("<red>Console cannot clear their own inventory, silly!".mm())
            return
        }
        sender.inventory.clear()
    }

    private fun applyWithTargets(sender: CommandSender, context: CommandContext) {
        context.get(targets).find(sender).forEach { target ->
            (target as Player).inventory.clear()
        }
    }

    private fun applyWithTargetsAndItem(sender: CommandSender, context: CommandContext) {
        val item = context.get(item).withAmount(Int.MAX_VALUE)
        context.get(targets).find(sender).forEach { target ->
            (target as Player).inventory.takeItemStack(item, TransactionOption.ALL)
        }
    }

    private fun applyWithTargetsAndItemAndMaxCount(sender: CommandSender, context: CommandContext) {
        val maxCount = context.get(maxCount)
        val item = context.get(item).withAmount(maxCount)
        context.get(targets).find(sender).forEach { target ->
            (target as Player).inventory.takeItemStack(item, TransactionOption.ALL)
        }
    }
}