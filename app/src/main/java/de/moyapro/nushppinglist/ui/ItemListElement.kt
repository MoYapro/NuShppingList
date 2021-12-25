package de.moyapro.nushppinglist.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.RemoveShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.moyapro.nushppinglist.constants.KATEGORY
import de.moyapro.nushppinglist.constants.SWITCHES
import de.moyapro.nushppinglist.constants.UNIT
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.CartItemProperties
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.ui.component.*
import de.moyapro.nushppinglist.ui.theme.Purple700

@Composable
fun ItemListElement(
    cartItem: CartItem,
    saveAction: (Item) -> Unit = {},
    addAction: (Item) -> Unit = {},
    subtractAction: (ItemId) -> Unit = {},
    deleteAction: (Item) -> Unit = {},
    editMode: Boolean = false,
    scrollIntoViewAction: () -> Unit = {},
) {
    var isEdited: Boolean by remember { mutableStateOf(editMode) }
    val item = cartItem.item

    Column(Modifier
        .fillMaxWidth()
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
                JustView(cartItem, addAction, subtractAction) {
                    isEdited = !isEdited
                    if (isEdited) scrollIntoViewAction()
                }
                if (isEdited) {
                    EditView(item, saveAction, deleteAction) { isEdited = false }
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
fun EditView(
    item: Item,
    saveAction: (Item) -> Unit,
    deleteAction: (Item) -> Unit,
    endEditMode: () -> Unit,
) {
    var editItem by remember { mutableStateOf(item) }
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        KategoryIndicator(editItem, 280.dp)
        Column(Modifier.fillMaxWidth()) {
            EditTextField(
                "Name",
                initialValue = editItem.name,
                onValueChange = { editItem = editItem.copy(name = it) },
            )
            Spacer(modifier = Modifier.height(4.dp))
            EditTextField(
                "Beschreibung",
                initialValue = editItem.description,
                onValueChange = { editItem = editItem.copy(description = it) },
            )
            Spacer(modifier = Modifier.height(4.dp))
            DecimalTextField(
                "Preis",
                initialValue = editItem.price,
                onValueChange = { editItem = editItem.copy(price = it) }
            )
            Spacer(modifier = Modifier.height(4.dp))
            NumberTextField(
                "Menge",
                initialValue = editItem.defaultItemAmount,
                onValueChange = { editItem = editItem.copy(defaultItemAmount = it) }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Dropdown(
                label = "Einheit",
                initialValue = editItem.defaultItemUnit,
                values = UNIT.values().toList(),
                onValueChange = { editItem = editItem.copy(defaultItemUnit = it) },
                itemLabel = { "${it.long} (${it.short})" }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Dropdown(
                label = "Kategorie",
                initialValue = editItem.kategory,
                values = KATEGORY.values().toList(),
                onValueChange = { editItem = editItem.copy(kategory = it) },
                itemLabel = { it.displayName }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .absolutePadding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        endEditMode()
                        deleteAction(editItem)
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Löschen")
                }
                Spacer(modifier = Modifier.width(Dp(4F)))
                Button(
                    onClick = {
                        endEditMode()
                        saveAction(editItem)
                    }) {
                    Icon(Icons.Filled.Done, contentDescription = "Hinzufügen")
                }
            }
        }
    }
}

@Composable
fun JustView(
    cartItem: CartItem,
    addAction: (Item) -> Unit,
    subtractAction: (ItemId) -> Unit = {},
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
        Row {
            KategoryIndicator(item)
            Spacer(modifier = Modifier.width(2.dp))
            Text(item.name)
        }
        Row {
            Button(
                modifier = Modifier.height(35.dp),
                onClick = { subtractAction(item.itemId) },
                colors = ButtonDefaults.buttonColors(backgroundColor = if (0 == cartItemProperties.amount) Color.Gray else Purple700),
            ) {
                Icon(Icons.Outlined.RemoveShoppingCart, contentDescription = "Löschen")
            }
            Button(
                modifier = Modifier.height(35.dp),
                onClick = { addAction(item) },
                colors = ButtonDefaults.buttonColors(backgroundColor = if (0 == cartItemProperties.amount) Color.Gray else Purple700),
            ) {
                Icon(Icons.Filled.AddShoppingCart, contentDescription = "Hinzufügen")
                Text(text = getAmountText(cartItemProperties).trim())
            }
        }
    }

}
