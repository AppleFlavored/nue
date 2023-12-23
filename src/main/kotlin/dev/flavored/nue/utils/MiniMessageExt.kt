package dev.flavored.nue.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

fun String.mm(): Component {
    return MiniMessage.miniMessage().deserialize(this)
}