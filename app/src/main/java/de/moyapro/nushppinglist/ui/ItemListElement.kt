package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import de.moyapro.nushppinglist.CartItemProperties
import de.moyapro.nushppinglist.Item
import de.moyapro.nushppinglist.SWITCHES


@Preview
@Composable
fun ItemListElement(
    @PreviewParameter(ItemProvider::class) item: Item,
    cartItemProperties: CartItemProperties? = null,
    saveAction: (Item) -> Unit = {},
    addAction: (Item) -> Unit = {},
    editMode: Boolean = false
) {
    var isEdited: Boolean by remember { mutableStateOf(editMode) }

    Column {
        if (SWITCHES.DEBUG) {
            Text(item.itemId.toString())
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                item.name,
                modifier = Modifier
                    .fillMaxWidth(.6F)
                    .clickable(onClick = { isEdited = !isEdited })
            )
            Button(onClick = { addAction(item) }
            ) {
                Text(text = "🛒 ${getAmountText(cartItemProperties)}".trim())
            }
        }
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

fun getAmountText(cartItemProperties: CartItemProperties?): String {
    if (null == cartItemProperties) {
        return ""
    }
    return "x ${cartItemProperties.amount}"
}
