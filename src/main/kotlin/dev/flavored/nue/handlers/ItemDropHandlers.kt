package dev.flavored.nue.handlers

import net.minestom.server.MinecraftServer
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.ItemEntity
import net.minestom.server.event.player.PlayerBlockBreakEvent
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material

object ItemDropHandlers {
    init {
        val globalEventHandler = MinecraftServer.getGlobalEventHandler()
        globalEventHandler.addListener(PlayerBlockBreakEvent::class.java, this::handleBlockBreak)
    }

    private fun handleBlockBreak(event: PlayerBlockBreakEvent) {
        if (event.player.gameMode != GameMode.SURVIVAL)
            return

        val itemStack = ItemStack.of(getReplacementMaterial(Material.fromNamespaceId(event.block.namespace())!!))
        val dropEntity = ItemEntity(itemStack)
        dropEntity.setInstance(event.instance, event.blockPosition.add(0.5))
    }

    private fun getReplacementMaterial(block: Material): Material = when (block) {
        Material.GRASS_BLOCK -> Material.DIRT
        Material.STONE -> Material.COBBLESTONE
        else -> block
    }
}