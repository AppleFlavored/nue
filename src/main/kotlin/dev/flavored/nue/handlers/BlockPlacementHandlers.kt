package dev.flavored.nue.handlers

import net.minestom.server.MinecraftServer
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.Direction

object BlockPlacementHandlers {
    init {
        val globalEventHandler = MinecraftServer.getGlobalEventHandler()
        globalEventHandler.addListener(PlayerBlockPlaceEvent::class.java, this::handleBlockPlace)
    }

    private fun calculateDirection(angle: Float): Direction {
        val yaw = (angle % 360 + 360) % 360

        val direction =
                when {
                    yaw > 135 || yaw < -135 -> Direction.NORTH
                    yaw < -45 -> Direction.EAST
                    yaw > 45 -> Direction.WEST
                    else -> Direction.SOUTH
                }

        return direction
    }

    private fun perspectiveToDirection(yaw: Float): Direction {
        return calculateDirection(yaw)
    }

    private fun handleBlockPlace(event: PlayerBlockPlaceEvent) {
        event.player.sendMessage("Block placed: ${event.block}")

        // Prevent torches from being placed on-top of eachother.
        // Prevent torches from being placed on the sides of eachother.
        // TODO: Fix torches placed from behind not having the right facing direction.

        val instance = event.player.instance

        val nearbyBlocks =
                listOf(
                        instance.getBlock(event.blockPosition.sub(1.0, 0.0, 0.0)),
                        instance.getBlock(event.blockPosition.add(1.0, 0.0, 0.0)),
                        instance.getBlock(event.blockPosition.sub(0.0, 0.0, 1.0)),
                        instance.getBlock(event.blockPosition.add(0.0, 0.0, 1.0)),
                        instance.getBlock(event.blockPosition.sub(0.0, 1.0, 0.0)),
                        instance.getBlock(event.blockPosition.add(0.0, 1.0, 0.0))
                )

        if (event.block == Block.TORCH && nearbyBlocks.any { it == Block.TORCH }) {
            event.isCancelled = true
        } else {

            if (event.block == Block.TORCH) {
                event.isCancelled = true
                
                val position = event.player.position 

                val direction = perspectiveToDirection(position.yaw()).opposite().toString()

                instance.setBlock(
                        event.blockPosition,
                        Block.WALL_TORCH.withProperty("facing", direction)
                )
            }
        }
    }
}
