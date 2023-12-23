package dev.flavored.nue

import cc.ekblad.toml.decode
import cc.ekblad.toml.tomlMapper
import dev.flavored.nue.commands.GameModeCommand
import dev.flavored.nue.commands.TeleportCommand
import dev.flavored.nue.commands.WhitelistCommand
import dev.flavored.nue.handlers.BlockPlacementHandlers
import dev.flavored.nue.handlers.ItemDropHandlers
import dev.flavored.nue.utils.mm
import net.hollowcube.polar.PolarLoader
import net.minestom.server.MinecraftServer
import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.coordinate.Pos
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.event.player.PlayerChatEvent
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.block.Block
import net.minestom.server.ping.ResponseData
import net.minestom.server.utils.NamespaceID
import net.minestom.server.world.DimensionType
import net.minestom.server.extras.MojangAuth
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.nio.file.Path
import kotlin.io.path.notExists
import kotlin.io.path.writeBytes
import kotlin.time.DurationUnit
import kotlin.time.toDuration

object Nue {
    val logger: Logger = LoggerFactory.getLogger(Nue::class.java)

    private val config: NueConfig
    private lateinit var defaultInstance: InstanceContainer

    init {
        val configPath = Path.of("config.toml")
        if (configPath.notExists()) {
            val defaultConfig = Nue::class.java.getResourceAsStream("/config.toml")!!
            configPath.writeBytes(defaultConfig.readAllBytes())
        }

        val mapper = tomlMapper {}
        config = mapper.decode(configPath)
    }

    private fun setupInstance() {
        val fullBright = DimensionType.builder(NamespaceID.from("nue:full_bright"))
            .ambientLight(2.0f)
            .build()
        MinecraftServer.getDimensionTypeManager().addDimension(fullBright)

        defaultInstance = MinecraftServer.getInstanceManager().createInstanceContainer(fullBright)
        defaultInstance.setGenerator { generator ->
            generator.modifier().fillHeight(-1, 0, Block.BEDROCK)
            generator.modifier().fillHeight(0, 3, Block.DIRT)
            generator.modifier().fillHeight(3, 4, Block.GRASS_BLOCK)
        }

        val worldPath = Path.of(config.world.path ?: "spawn.polar")
        defaultInstance.chunkLoader = PolarLoader(worldPath)
    }

    private fun registerEventListeners() {
        val eventHandler = MinecraftServer.getGlobalEventHandler()

        eventHandler.addListener(PlayerChatEvent::class.java) { event -> 
            event.setChatFormat { _ -> 
                "<light_purple>${event.player.username}<reset>: ${event.message}".mm()
            }
        }

        eventHandler.addListener(PlayerLoginEvent::class.java) { event ->
            event.setSpawningInstance(defaultInstance)

            logger.info("${event.player.username} joined")
            Audiences.players().sendMessage("<yellow>${event.player.username} joined.".mm())
        }
        eventHandler.addListener(PlayerDisconnectEvent::class.java) { event ->
            logger.info("${event.player.username} disconnected")
            Audiences.players().sendMessage("<yellow>${event.player.username} left.".mm())
        }
        eventHandler.addListener(PlayerSpawnEvent::class.java) { event ->
            event.player.teleport(Pos(0.0, 4.0, 0.0))
        }
        eventHandler.addListener(ServerListPingEvent::class.java) { event ->
            event.responseData = ResponseData().apply {
                description = (config.server.motd ?: "A Minecraft Server").mm()
                config.server.maxPlayers?.let { maxPlayer = it }
                config.server.favicon?.let { favicon = it }
            }
        }

        BlockPlacementHandlers
        ItemDropHandlers
    }

    private fun registerCommands() {
        MinecraftServer.getCommandManager().apply {
            register(GameModeCommand())
            register(TeleportCommand())
            register(WhitelistCommand())
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val startTime = System.currentTimeMillis()

        // Set a default uncaught exception handler for logging.
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            logger.error("Exception in thread \"${thread.name}\":", throwable)
        }

        val server = MinecraftServer.init()
        MojangAuth.init()
        
        setupInstance()
        registerEventListeners()
        registerCommands()

        MinecraftServer.getSchedulerManager().buildShutdownTask {
            logger.info("Saving chunks to {}", config.world.path ?: "spawn.polar")
            defaultInstance.saveChunksToStorage().join()
        }

        val endpoint = InetSocketAddress(config.server.address, config.server.port)
        server.start(endpoint)

        val startupTime = (System.currentTimeMillis() - startTime).toDuration(DurationUnit.MILLISECONDS)
        logger.info("Started server on *:${endpoint.port} ($startupTime)")
    }
}