package dev.flavored.nue.handlers

import net.minestom.server.MinecraftServer
import net.minestom.server.event.player.PlayerBlockPlaceEvent

object BlockPlacementHandlers {
    init {
        val globalEventHandler = MinecraftServer.getGlobalEventHandler()
        globalEventHandler.addListener(PlayerBlockPlaceEvent::class.java, this::handleBlockPlace)
    }

    private fun handleBlockPlace(event: PlayerBlockPlaceEvent) {
        event.player.sendMessage("Block placed: ${event.block}")
    }
}