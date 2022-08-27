package de.moyapro.nushppinglist.sync

object MqttSingleton {
    val adapter: MqttServiceAdapter by lazy { MqttServiceAdapter() }
}
