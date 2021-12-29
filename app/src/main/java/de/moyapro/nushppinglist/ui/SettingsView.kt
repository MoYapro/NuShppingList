package de.moyapro.nushppinglist.ui

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.moyapro.nushppinglist.MainActivity
import de.moyapro.nushppinglist.constants.SETTING
import de.moyapro.nushppinglist.constants.SWITCHES
import de.moyapro.nushppinglist.ui.component.settings.BooleanSettings

@Composable
fun SettingsView() {
    val preferences: SharedPreferences = MainActivity.preferences

    Column() {
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

        Spacer(modifier = Modifier.height(20.dp))
        BooleanSettings(setting = SETTING.CLEAR_AFTER_ADD, preferences = preferences)
//        CartIdentSettings(setting = SETTING.CART_IDENT, preferences)
    }
}
