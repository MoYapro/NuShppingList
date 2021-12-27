package de.moyapro.nushppinglist.ui.component.settings

import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import de.moyapro.nushppinglist.MainActivity
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.constants.SETTING
import de.moyapro.nushppinglist.ui.component.EditTextField
import java.util.*

@Composable
fun CartIdentSettings(setting: SETTING, preferences: SharedPreferences) {

    val clipboardManager = MainActivity.clipboardManager
    val context = LocalContext.current
    var currentIdentValue by remember {
        mutableStateOf(preferences.getString(setting.name, "") ?: "")
    }
    Surface() {
        Column() {

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
                    Text(
                        text = currentIdentValue,
                        modifier = Modifier.alpha(CONSTANTS.MUTED_ALPHA)
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EditTextField(
                    modifier = Modifier.fillMaxWidth(.8F),
                            initialValue = currentIdentValue,
                    onValueChange = {
                        currentIdentValue = it
                        preferences.edit().putString(setting.name, currentIdentValue).apply()
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = {
                        currentIdentValue = UUID.randomUUID().toString()
                        preferences.edit().putString(setting.name, currentIdentValue).apply()
                    },
                    modifier = Modifier.fillMaxWidth(.8F)
                ) {
                    Text(text = "Neue Kennzeichnung")
                }
            }
        }
    }

}
