package dev.flavored.nue.handlers

import net.minestom.server.MinecraftServer
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.Direction

object BlockPlacementHandlers {
    val unsupportedTorchSurfaces = listOf(Block.TORCH, Block.WALL_TORCH)

    init {
        val globalEventHandler = MinecraftServer.getGlobalEventHandler()
        globalEventHandler.addListener(PlayerBlockPlaceEvent::class.java, this::handleBlockPlace)
    }

    private fun handleBlockPlace(event: PlayerBlockPlaceEvent) {
        // Prevent torches from being placed on-top of eachother.
        // TODO: Prevent torches from being placed on the sides of eachother.
        val instance = event.player.instance

        if (event.block == Block.TORCH &&
                        // Checks if the block below is in the unsupported surfaces list.
                        unsupportedTorchSurfaces.contains(
                                instance.getBlock(event.blockPosition.sub(0.0, 1.0, 0.0))
                        )
        ) {
            event.isCancelled = true
        } else {
            val direction = event.blockFace.toDirection().toString().lowercase()
            val validDirections =
                    Direction.HORIZONTAL.map { direction -> direction.toString().lowercase() }

            val opposite = event.blockFace.toDirection().opposite()
            val isValidPlacement =
                    !unsupportedTorchSurfaces.contains(
                            instance.getBlock(
                                    event.blockPosition.add(
                                            opposite.normalX().toDouble(),
                                            opposite.normalY().toDouble(),
                                            opposite.normalZ().toDouble()
                                    )
                            )
                    )

            if (event.block == Block.TORCH &&
                            validDirections.contains(direction) &&
                            isValidPlacement
            ) {
                event.isCancelled = true

                instance.setBlock(
                        event.blockPosition,
                        Block.WALL_TORCH.withProperty("facing", direction)
                )

                // TODO: Place on neighboring valid surface if the provided position is illegal.
            }
        }
    }
}
