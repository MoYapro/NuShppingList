package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import de.moyapro.nushppinglist.Item

@Composable
fun ItemListElement(item: Item) {
    var isEdited: Boolean by remember {
        mutableStateOf(false)
    }
    Text(
        item.name,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { isEdited = !isEdited })
    )
    if (isEdited) {
        Button(onClick = { isEdited = !isEdited }) {
            Text("Save")
        }
    }
}