package de.moyapro.nushppinglist.ui.component.settings

import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.constants.SETTING
import de.moyapro.nushppinglist.ui.component.EditTextField

@Composable
fun StringSettings(
    setting: SETTING,
    preferences: SharedPreferences,
    immediateApplyAction: (String) -> Unit = {},
) {
    var currentValue: String by remember {
        mutableStateOf(preferences.getString(setting.name, "") ?: "")
    }
    var isFocused: Boolean by remember { mutableStateOf(false) }

    Surface() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.Absolute.SpaceBetween
        ) {
            Column(Modifier.fillMaxWidth()) {
                EditTextField(label = setting.label,
                    initialValue = currentValue,
                    onValueChange = { newValue ->
                        currentValue = newValue
                        immediateApplyAction(newValue)
                        preferences.edit().putString(setting.name, newValue).apply()
                    },
                    onFocusChanged = { focusState -> isFocused = focusState.isFocused }
                )
                if (isFocused) {
                    Text(
                        text = setting.description,
                        modifier = Modifier.alpha(CONSTANTS.MUTED_ALPHA)
                    )
                }
            }
        }
    }

}
