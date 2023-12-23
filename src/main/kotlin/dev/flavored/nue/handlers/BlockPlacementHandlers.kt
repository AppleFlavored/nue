package dev.flavored.nue.handlers

import net.minestom.server.MinecraftServer
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.utils.Direction
import net.minestom.server.instance.block.Block

object BlockPlacementHandlers {
    init {
        val globalEventHandler = MinecraftServer.getGlobalEventHandler()
        globalEventHandler.addListener(PlayerBlockPlaceEvent::class.java, this::handleBlockPlace)
    }

    private fun handleBlockPlace(event: PlayerBlockPlaceEvent) {
        event.player.sendMessage("Block placed: ${event.block}")

            // Prevent torches from being placed on-top of eachother.
            // Prevent torches from being placed on the sides of eachother.
            // TODO: Fix torches placed from behind not having the right facing direction. 

            val instance = event.player.instance 

            val above = event.blockPosition.sub(0.0, 1.0, 0.0)
            val below = event.blockPosition.add(0.0, 1.0, 0.0)
            val front = event.blockPosition.sub(1.0, 0.0, 0.0)
            val left = event.blockPosition.sub(0.0, 0.0, 1.0)
            val right = event.blockPosition.add(0.0, 0.0, 1.0)

            if(
                event.block == Block.TORCH &&
                (
                    instance.getBlock(front) == Block.TORCH ||
                    instance.getBlock(left) == Block.TORCH ||
                    instance.getBlock(right) == Block.TORCH ||
                    instance.getBlock(below) == Block.TORCH || 
                    instance.getBlock(above) == Block.TORCH
                )
            ) {
                event.isCancelled = true 
            } else {
                // SO: https://stackoverflow.com/questions/35831619/get-the-direction-a-player-is-looking
                val calculateDirection: (Float) -> Direction = { angle ->
                    val yaw = (angle % 360 + 360) % 360
                    when {
                        yaw > 135 || yaw < -135 -> Direction.NORTH
                        yaw < -45 -> Direction.EAST
                        yaw > 45 -> Direction.WEST
                        else -> Direction.SOUTH
                    }
                }

                if(event.block == Block.TORCH) {
                    event.isCancelled = true
                    val yaw = event.player.position.yaw()
                    val direction = calculateDirection(yaw).opposite().toString().lowercase()
                    instance.setBlock(event.blockPosition, Block.WALL_TORCH.withProperty("facing", direction))
                }
            }
    }
}