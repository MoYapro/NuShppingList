package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.moyapro.nushppinglist.constants.SWITCHES
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.CartItemProperties
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.ui.theme.Purple700

@Composable
fun ItemListElement(
    cartItem: CartItem,
    saveAction: (Item) -> Unit = {},
    addAction: (Item) -> Unit = {},
    editMode: Boolean = false,
) {
    var isEdited: Boolean by remember { mutableStateOf(editMode) }
    val item = cartItem.item

    Column(Modifier.fillMaxWidth()) {
        if (SWITCHES.DEBUG) {
            Text(item.itemId.toString())
        }

        Surface(
            elevation = 3.dp,
        ) {
            Column {
                JustView(cartItem, addAction) { isEdited = !isEdited }
                if (isEdited) {
                    EditView(item, saveAction) { isEdited = false }
                }
            }
        }
    }
}

fun getAmountText(cartItemProperties: CartItemProperties?): String {
    if (null == cartItemProperties || 0 == cartItemProperties.amount) {
        return ""
    }
    return "x ${cartItemProperties.amount}"
}

@Composable
fun EditView(item: Item, saveAction: (Item) -> Unit, endEditMode: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        var editItem by remember { mutableStateOf(item) }
        Column() {

            EditTextField(
                "Name",
                initialValue = editItem.name,
                onValueChange = { editItem = editItem.copy(name = it) }
            )
            EditTextField(
                "Einheit",
                initialValue = editItem.defaultItemUnit,
                onValueChange = { editItem = editItem.copy(defaultItemUnit = it) }
            )
            NumberTextField(
                "Preis",
                initialValue = editItem.price,
                onValueChange = { editItem = editItem.copy(price = it) }
            )
            Button(onClick = {
                endEditMode()
                saveAction(editItem)
            }) {
                Text("Save")
            }
        }
    }
}

@Composable
fun JustView(
    cartItem: CartItem,
    addAction: (Item) -> Unit,
    beginEditMode: () -> Unit,
) {
    val item = cartItem.item
    val cartItemProperties = cartItem.cartItemProperties
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(1.dp)
    ) {
        Text(
            item.name,
            modifier = Modifier
                .fillMaxWidth(.6F)
                .clickable(onClick = beginEditMode)
        )
        Button(
            onClick = { addAction(item) },
            colors = ButtonDefaults.buttonColors(backgroundColor = if (0 == cartItemProperties.amount) Color.Gray else Purple700),
        ) {
            Text(text = "🛒 ${getAmountText(cartItemProperties)}".trim())
        }
    }

}
