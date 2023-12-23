package dev.flavored.nue.commands

import dev.flavored.nue.utils.mm
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.CommandExecutor
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Player

class TeleportCommand : Command("teleport", "tp") {
    private val targets = ArgumentType.Entity("targets")
    private val destination = ArgumentType.Entity("destination")
        .onlyPlayers(true)
        .singleEntity(true)
    private val location = ArgumentType.RelativeVec3("location")

    init {
        defaultExecutor = CommandExecutor { sender, _ ->
            sender.sendMessage("<red>Usage: /teleport".mm())
        }
        addSyntax(this::applyWithDestination, destination)
        addSyntax(this::applyWithTargetsAndDestination, targets, destination)
        addSyntax(this::applyWithLocation, location)
        addSyntax(this::applyWithTargetsAndLocation, targets, location)
        // TODO: Add permission check
    }

    private fun applyWithDestination(sender: CommandSender, context: CommandContext) {
        if (sender !is Player) {
            sender.sendMessage("<red>Console cannot teleport themselves, silly!".mm())
            return
        }

        val destinationEntity = context.get(destination).findFirstEntity(sender)
        if (destinationEntity == null) {
            sender.sendMessage("<red>Invalid destination!".mm())
            return
        }

        if (destinationEntity.instance == sender.instance) {
            sender.teleport(destinationEntity.position)
        } else {
            sender.setInstance(destinationEntity.instance, destinationEntity.position)
        }
    }

    private fun applyWithTargetsAndDestination(sender: CommandSender, context: CommandContext) {
        val destinationEntity = context.get(destination).findFirstEntity(sender)
        if (destinationEntity == null) {
            sender.sendMessage("<red>Invalid destination!".mm())
            return
        }

        context.get(targets).find(sender).forEach { target ->
            if (target.instance == destinationEntity.instance) {
                target.teleport(destinationEntity.position)
            } else {
                target.setInstance(destinationEntity.instance, destinationEntity.position)
            }
        }
    }

    private fun applyWithLocation(sender: CommandSender, context: CommandContext) {
        if (sender !is Player) {
            sender.sendMessage("<red>Console cannot teleport themselves, silly!".mm())
            return
        }

        val location = context.get(location).fromSender(sender).asPosition()
        sender.teleport(sender.position.withCoord(location))
    }

    private fun applyWithTargetsAndLocation(sender: CommandSender, context: CommandContext) {
        val location = context.get(location).fromSender(sender).asPosition()
        context.get(targets).find(sender).forEach { target ->
            target.teleport(target.position.withCoord(location))
        }
    }
}