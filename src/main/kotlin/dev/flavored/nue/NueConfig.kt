package dev.flavored.nue

data class NueConfig(
    val server: Server,
    val world: World,
) {
    data class Server(
        val address: String = "127.0.0.1",
        val port: Int = 25565,
        val maxPlayers: Int?,
        val motd: String?,
        val favicon: String?
    )

    data class World(
        val path: String?,
    )
}