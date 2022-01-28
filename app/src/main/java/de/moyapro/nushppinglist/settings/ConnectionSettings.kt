package de.moyapro.nushppinglist.settings

data class ConnectionSettings(
    val syncEnabled:Boolean,
    val hostname: String,
    val port: Int,
    val username: String,
    val password: String,
    val topic: String,
    val useTls: Boolean,
)
