package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import de.moyapro.nushppinglist.Item

@Preview
@Composable
fun ItemListElement(@PreviewParameter(ItemProvider::class) item: Item, editMode: Boolean = true) {
    var isEdited: Boolean by remember { mutableStateOf(editMode) }
    Text(
        item.name,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { isEdited = !isEdited })
    )
    if (isEdited) {
        Text("edit")
        val textState = remember { mutableStateOf("") }
        EditTextField(
            "name",
            initialValue = textState.value,
            onValueChange = { textState.value = it }
        )
        Button(onClick = { isEdited = !isEdited }) {
            Text("Save")
        }
    }
}