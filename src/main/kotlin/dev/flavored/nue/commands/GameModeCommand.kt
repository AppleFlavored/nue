package dev.flavored.nue.commands

import dev.flavored.nue.utils.mm
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.CommandExecutor
import net.minestom.server.command.builder.arguments.ArgumentEnum
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player

class GameModeCommand : Command("gamemode", "gm") {
    private val gameMode = ArgumentType.Enum("gamemode", GameMode::class.java)
        .setFormat(ArgumentEnum.Format.LOWER_CASED)
    private val target = ArgumentType.Entity("target")
        .onlyPlayers(true)
        .singleEntity(true)

    init {
        defaultExecutor = CommandExecutor { sender, _ ->
            sender.sendMessage("<red>Usage: /gamemode <survival/creative/adventure/spectator> [target]".mm())
        }
        addSyntax(this::applyDefault, gameMode)
        addSyntax(this::applyWithTarget, gameMode, target)
        // TODO: Add permission check
    }

    private fun applyDefault(sender: CommandSender, context: CommandContext) {
        if (sender !is Player) {
            sender.sendMessage("<red>Console cannot set their own game mode, silly!".mm())
            return
        }
        sender.gameMode = context.get(gameMode)
    }

    private fun applyWithTarget(sender: CommandSender, context: CommandContext) {
        val targetPlayer = context.get(target).findFirstPlayer(sender)
        if (targetPlayer == null || !targetPlayer.isOnline) {
            sender.sendMessage("<red>Player not found!".mm())
            return
        }
        targetPlayer.gameMode = context.get(gameMode)
    }
}