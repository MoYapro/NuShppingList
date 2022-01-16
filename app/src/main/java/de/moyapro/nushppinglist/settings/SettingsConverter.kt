package de.moyapro.nushppinglist.settings

import android.content.SharedPreferences
import de.moyapro.nushppinglist.constants.SETTING

object SettingsConverter {
    fun toConnectionSettings(preferences: SharedPreferences): ConnectionSettings {
        val (hostname, port) = splitHostnamePort(preferences.getString(SETTING.SYNC_MQTT_SERVER_HOSTNAME.name,
            "") ?: "")
        return ConnectionSettings(
            hostname = hostname,
            port = port,
            username = preferences.getString(SETTING.SYNC_MQTT_SERVER_USER.name, "") ?: "",
            password = preferences.getString(SETTING.SYNC_MQTT_SERVER_PASSWORD.name, "") ?: "",
            topic = preferences.getString(SETTING.SYNC_MQTT_SERVER_BASE_TOPIC.name, "") ?: "",
            useTls = preferences.getBoolean(SETTING.SYNC_MQTT_SERVER_TLS.name, true),
        )
    }

    fun splitHostnamePort(hostnameAndPort: String): Pair<String, Int> {
        val (hostname, port) = hostnameAndPort.split(":")
        return Pair(hostname, port.toInt())
    }
}
