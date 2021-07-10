package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import de.moyapro.nushppinglist.Item
import de.moyapro.nushppinglist.SWITCHES

@Preview
@Composable
fun ItemListElement(
    @PreviewParameter(ItemProvider::class) item: Item,
    saveAction: (Item) -> Unit = {},
    editMode: Boolean = false
) {
    var isEdited: Boolean by remember { mutableStateOf(editMode) }
    Column {
        if (SWITCHES.DEBUG) {
            Text(item.id.toString())
        }
        Text(
            item.name,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { isEdited = !isEdited })
        )
        if (isEdited) {
            val textState = remember { mutableStateOf(item.name) }
            EditTextField(
                "Name",
                initialValue = textState.value,
                onValueChange = { textState.value = it }
            )
            Button(onClick = {
                isEdited = !isEdited
                saveAction(item.copy(name = textState.value))
            }) {
                Text("Save")
            }
        }
    }
}