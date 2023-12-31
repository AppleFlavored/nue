# Nue
Nue is a vanilla server reimplementation using [Minestom:CE](https://github.com/hollow-cube/minestom-ce).

## Configuration
Configuring Nue is done through the `config.toml` file. This file is autogenerated when the
server JAR is first ran. The default values can be found [here](src/main/resources/config.toml).

## Commands
At the moment, Nue only supports the following commands:

* `/clear`
* `/teleport` (alias: `/tp`)
* `/gamemode` (alias: `/gm`)
* `/give`
* `/whitelist`

## Building
Before building, make sure you have Java 17 (or later) installed.

To build Nue, use the provided gradle wrapper and run the `shadowJar` task:
```shell
./gradlew shadowJar
```

## License
Nue is licensed under the [MIT License](https://opensource.org/license/mit/).
See the [LICENSE](LICENSE) file for more details.