package de.moyapro.nushppinglist.ui

import androidx.compose.animation.animateContentSize
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
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.constants.SWITCHES
import de.moyapro.nushppinglist.constants.UNIT
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.CartItemProperties
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.ui.component.Dropdown
import de.moyapro.nushppinglist.ui.component.EditTextField
import de.moyapro.nushppinglist.ui.component.NumberTextField
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

    Column(Modifier
        .fillMaxWidth()
        .padding(4.dp)
    ) {
        if (SWITCHES.DEBUG) {
            Text(item.itemId.toString())
        }

        Surface(
            elevation = 3.dp,
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
            ) {
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
            Dropdown(
                label = "Einheit",
                initialValue = editItem.defaultItemUnit,
                values = UNIT.values().toList(),
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .absolutePadding(left = 4.dp)
            .fillMaxWidth()
            .clickable(onClick = beginEditMode)
    ) {
        Text(
            item.name,
        )
        Button(
            onClick = { addAction(item) },
            colors = ButtonDefaults.buttonColors(backgroundColor = if (0 == cartItemProperties.amount) Color.Gray else Purple700),
        ) {
            Text(text = "${CONSTANTS.CART_CHAR} ${getAmountText(cartItemProperties)}".trim())
        }
    }

}
