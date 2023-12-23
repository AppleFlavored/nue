plugins {
    kotlin("jvm") version "1.9.21"
    id("application")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.flavored"
version = "0.1.0"

application {
    mainClass.set("dev.flavored.nue.Nue")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("dev.hollowcube:minestom-ce:1554487748")
    implementation("dev.hollowcube:polar:1.3.2")
    implementation("net.kyori:adventure-text-minimessage:4.14.0")
    implementation("cc.ekblad:4koma:1.2.0")
    implementation("ch.qos.logback:logback-classic:1.4.14")
}

kotlin {
    jvmToolchain(17)
}