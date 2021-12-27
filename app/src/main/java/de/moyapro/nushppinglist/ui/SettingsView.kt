package de.moyapro.nushppinglist.ui

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceBetween
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.constants.SETTINGS
import de.moyapro.nushppinglist.constants.SWITCHES

@Composable
fun SettingsView(preferences: SharedPreferences) {

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
        SETTINGS.values().forEach { setting ->
            Surface() {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = SpaceBetween
                ) {
                    Column(Modifier.fillMaxWidth(.8F)) {
                        Text(text = setting.label)
                        Text(
                            text = setting.description,
                            modifier = Modifier.alpha(CONSTANTS.MUTED_ALPHA)
                        )
                    }
                    var checkedState by remember { mutableStateOf(preferences.getBoolean(setting.name, false)) }
                    Switch(
                        checked = checkedState,
                        onCheckedChange = { preferences.edit().putBoolean(setting.name, it).apply() }
                    )
                }
            }
        }
    }
}
