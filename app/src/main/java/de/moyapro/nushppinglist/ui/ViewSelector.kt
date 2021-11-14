package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import de.moyapro.nushppinglist.constants.CONSTANTS.ACTIVE
import de.moyapro.nushppinglist.constants.CONSTANTS.IN_ACTIVE
import de.moyapro.nushppinglist.ui.theme.Purple700

enum class MainView(val text: String) {
    EINKAUFSLISTE("Einkaufsliste"),
    DINGE("Dinge"),
    REZEPTE("Rezepte"),
}

@Composable
fun ViewSelector(selectedView: MainView, setNewState: (MainView) -> Unit) {
    Row {
        MainView.values().forEachIndexed { index, view ->
            val isActive = selectedView == view
            Button(
                modifier = Modifier
                    .fillMaxWidth(1F / (MainView.values().size - index))
                    .semantics {
                        contentDescription = if (isActive) ACTIVE else IN_ACTIVE
                    },
                colors = ButtonDefaults.buttonColors(backgroundColor = if (isActive) Purple700 else Color.Gray),
                onClick = {
                    setNewState(view)
                }) {
                Text(view.text)
            }
            Spacer(modifier = Modifier.width(Dp(1F)))
        }
    }
}
