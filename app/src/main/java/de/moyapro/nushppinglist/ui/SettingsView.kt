package de.moyapro.nushppinglist.ui

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.moyapro.nushppinglist.MainActivity
import de.moyapro.nushppinglist.constants.SETTING
import de.moyapro.nushppinglist.constants.SWITCHES
import de.moyapro.nushppinglist.ui.component.settings.BooleanSettings
import de.moyapro.nushppinglist.ui.component.settings.StringSettings

@Composable
fun SettingsView() {
    val preferences: SharedPreferences = MainActivity.preferences ?: return
    var isSyncEnabled by remember {
        mutableStateOf(preferences.getBoolean(SETTING.SYNC_ENABLED.name,
            false))
    }
    val scrollState = rememberScrollState()

    Column(Modifier.verticalScroll(scrollState)) {
        DebugRawSettings(preferences)

        Spacer(modifier = Modifier.height(20.dp))
        BooleanSettings(SETTING.CLEAR_AFTER_ADD, preferences)
        Spacer(modifier = Modifier
            .height(2.dp)
            .fillMaxWidth()
            .background(if (isSyncEnabled) Color.LightGray else MaterialTheme.colors.surface))
        BooleanSettings(SETTING.SYNC_ENABLED, preferences) { newValue ->
            isSyncEnabled = newValue
        }
        if (isSyncEnabled) {
            StringSettings(SETTING.SYNC_MQTT_SERVER_HOSTNAME, preferences)
            StringSettings(SETTING.SYNC_MQTT_SERVER_BASE_TOPIC, preferences)
            StringSettings(SETTING.SYNC_MQTT_SERVER_USER, preferences)
            StringSettings(SETTING.SYNC_MQTT_SERVER_PASSWORD, preferences)
            BooleanSettings(SETTING.SYNC_MQTT_SERVER_TLS, preferences)
        }
        Spacer(modifier = Modifier
            .height(2.dp)
            .fillMaxWidth()
            .background(if (isSyncEnabled) Color.LightGray else MaterialTheme.colors.surface))
        Spacer(modifier = Modifier.height(250.dp))
    }
}

@Composable
private fun DebugRawSettings(preferences: SharedPreferences?) {
    if (null == preferences) {
        Text("Preferences are not set: null")
        return
    }
    if (SWITCHES.DEBUG)
        for ((settingsKey, settingsValue) in preferences.all) {
            Row(
                modifier = Modifier.background(MaterialTheme.colors.surface),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                val textColor = contentColorFor(backgroundColor = MaterialTheme.colors.surface)
                Text(text = settingsKey, color = textColor)
                Text(text = "->", color = textColor)
                Text(text = settingsValue.toString(), color = textColor)
            }
        }
}
