package de.moyapro.nushppinglist.settings

import android.content.SharedPreferences
import de.moyapro.nushppinglist.constants.SETTING

object SettingsConverter {
    val INVALID_CONNECTION_SETTINGS: ConnectionSettings =
        ConnectionSettings(false,"", -1, "", "", "", false)

    fun toConnectionSettings(preferences: SharedPreferences): ConnectionSettings {
        val connectionSettings = buildConnectionSettingsFromPreferences(preferences)
        return if (isValid(connectionSettings)) connectionSettings else INVALID_CONNECTION_SETTINGS
    }

    private fun isValid(connectionSettings: ConnectionSettings): Boolean {
        val hostnameValid = connectionSettings.hostname.isNotBlank()
        val portValid = connectionSettings.port in 1000..65535
        val topicValid = connectionSettings.topic.isNotBlank()
        return hostnameValid && portValid && topicValid
    }

    private fun buildConnectionSettingsFromPreferences(preferences: SharedPreferences): ConnectionSettings {
        val (hostname, port) = splitHostnamePort(preferences.getString(SETTING.SYNC_MQTT_SERVER_HOSTNAME.name,
            "") ?: "")
        return ConnectionSettings(
            syncEnabled = preferences.getBoolean(SETTING.SYNC_ENABLED.name, false) ,
            hostname = hostname,
            port = port,
            username = preferences.getString(SETTING.SYNC_MQTT_SERVER_USER.name, "") ?: "",
            password = preferences.getString(SETTING.SYNC_MQTT_SERVER_PASSWORD.name, "") ?: "",
            topic = preferences.getString(SETTING.SYNC_MQTT_SERVER_BASE_TOPIC.name, "") ?: "",
            useTls = preferences.getBoolean(SETTING.SYNC_MQTT_SERVER_TLS.name, true),
        )
    }

    fun splitHostnamePort(hostnameAndPort: String): Pair<String, Int> {
        val parts = hostnameAndPort.split(":")
        if (parts.size != 2) {
            return Pair("", -1)
        }
        val (hostname, port) = parts
        return Pair(hostname, port.toInt())
    }
}
