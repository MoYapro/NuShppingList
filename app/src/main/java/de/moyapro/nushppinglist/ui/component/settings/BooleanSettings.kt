package de.moyapro.nushppinglist.ui.component.settings

import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.constants.SETTING

@Composable
fun BooleanSettings(setting: SETTING, preferences: SharedPreferences, immediateApplyAction: (Boolean) -> Unit = {}) {
    Surface() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.Absolute.SpaceBetween
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
                onCheckedChange = { newValue ->
                    checkedState = newValue
                    immediateApplyAction(newValue)
                    preferences.edit().putBoolean(setting.name, newValue).apply()
                }
            )
        }
    }

}
