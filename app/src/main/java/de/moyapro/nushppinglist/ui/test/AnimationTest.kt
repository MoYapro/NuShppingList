package de.moyapro.nushppinglist.ui.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.moyapro.nushppinglist.ui.component.HoldableButton

@Composable
fun AnimationTest() {
    Column() {
        var longPressed by remember { mutableStateOf(false) }
        val longPressColor: Color = if (longPressed) Color.Green else Color.Red
        val longPressSize: Dp = 35.dp

        Box(modifier = Modifier
            .width(longPressSize)
            .height(longPressSize)
            .background(longPressColor))

        HoldableButton(
            onLongPress = { longPressed = !longPressed },
        ) {
            Text(text = "Hold me!")
        }
    }
}

