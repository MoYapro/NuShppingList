package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ColorTest() {
    val colors = listOf(
        Pair(MaterialTheme.colors.primary, "primary"),
        Pair(MaterialTheme.colors.primaryVariant, "primaryVariant"),
        Pair(MaterialTheme.colors.secondary, "secondary"),
        Pair(MaterialTheme.colors.secondaryVariant, "secondaryVariant"),
        Pair(MaterialTheme.colors.background, "background"),
        Pair(MaterialTheme.colors.surface, "surface"),
        Pair(MaterialTheme.colors.error, "error"),
    )
    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()) {

        Spacer(Modifier.height(4.dp))
        Button(onClick = {}) {
            Icon(Icons.Filled.AddShoppingCart, contentDescription = "XXX")
            Text(text = "DefaultButton")
        }
        Spacer(Modifier.height(4.dp))
        Surface(Modifier
            .height(60.dp)
            .width(300.dp)) {
            Text("Default surface")
        }
        Spacer(Modifier.height(4.dp))

        colors.forEach { (color, name) ->
            Box(Modifier
                .height(80.dp)
                .width(300.dp)
                .background(color)) {
                Text(
                    text = "$name, $color",
                    color = contentColorFor(backgroundColor = color))
            }
        }
    }
}
