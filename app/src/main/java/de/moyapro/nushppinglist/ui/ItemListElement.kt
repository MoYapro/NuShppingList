package de.moyapro.nushppinglist.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RemoveShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
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

@Composable
fun ItemListElement(
    cartItem: CartItem,
    viewLocked: Boolean = false,
    toggleCheckAction: (CartItemProperties) -> Unit = {},
    saveAction: (Item) -> Unit = {},
    addAction: (Item) -> Unit = {},
    subtractAction: (ItemId) -> Unit = {},
    deleteAction: (Item) -> Unit = {},
    editMode: Boolean = false,
    scrollIntoViewAction: () -> Unit = {},
) {
    var isEdited: Boolean by remember { mutableStateOf(editMode) }
    val item = cartItem.item

    Column(
        Modifier
            .fillMaxWidth()
    ) {
        if (SWITCHES.DEBUG) {
            Text(item.itemId.toString())
            Text(cartItem.cartItemProperties.inCart.toString())
            Text("checked: ${cartItem.cartItemProperties.checked}")
        }
        Surface(
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                if (viewLocked) {
                    SimpleView(cartItem, toggleCheckAction)
                } else
                    FullView(cartItem, addAction, toggleCheckAction, subtractAction) {
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
        val saveActionClosure = {
            endEditMode()
            saveAction(editItem)
        }
        KategoryIndicator(editItem, 280.dp)
        Column(Modifier.fillMaxWidth()) {
            EditTextField(
                label = "Name",
                initialValue = editItem.name,
                onValueChange = { editItem = editItem.copy(name = it) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                doneAction = saveActionClosure
            )
            Spacer(modifier = Modifier.height(4.dp))
            EditTextField(
                label = "Beschreibung",
                initialValue = editItem.description,
                onValueChange = { editItem = editItem.copy(description = it) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                doneAction = saveActionClosure
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                DecimalTextField(
                    "Preis",
                    initialValue = editItem.price,
                    onValueChange = { editItem = editItem.copy(price = it) },
                    modifier = Modifier.fillMaxWidth(.5F),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    doneAction = saveActionClosure
                )
                Spacer(modifier = Modifier.width(4.dp))
                NumberTextField(
                    label = "Menge",
                    initialValue = editItem.defaultItemAmount,
                    onValueChange = { editItem = editItem.copy(defaultItemAmount = it) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    doneAction = saveActionClosure
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                Dropdown(
                    label = "Einheit",
                    initialValue = editItem.defaultItemUnit,
                    values = UNIT.values().toList(),
                    onValueChange = { editItem = editItem.copy(defaultItemUnit = it) },
                    itemLabel = { "${it.long} (${it.short})" },
                    modifier = Modifier.fillMaxWidth(.5F)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Dropdown(
                    label = "Kategorie",
                    initialValue = editItem.kategory,
                    values = KATEGORY.values().toList(),
                    onValueChange = { editItem = editItem.copy(kategory = it) },
                    itemLabel = { it.displayName },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .absolutePadding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HoldableButton(
                    onLongPress = {
                        endEditMode()
                        deleteAction(editItem)
                    },
                    holdHintText = "Halten zum Löschen",
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Löschen")
                }
                Spacer(modifier = Modifier.width(Dp(4F)))
                Button(
                    shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp),
                    onClick = saveActionClosure
                ) {
                    Icon(Icons.Filled.Done, contentDescription = "Hinzufügen")
                }
            }
            Spacer(Modifier.height(30.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SimpleView(
    cartItem: CartItem,
    toggleCheckAction: (CartItemProperties) -> Unit,
) {
    Spacer(Modifier.height(3.dp))
    val (cartItemProperties, item) = cartItem
    val alpha = if (cartItemProperties.checked) .7F else 1F
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .absolutePadding(left = 4.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (cartItemProperties.amount > 0) toggleCheckAction(cartItemProperties)
                }
            )
            .alpha(alpha)
    ) {
        KategoryIndicator(cartItem.item)
        Spacer(Modifier.width(3.dp))
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                InfoText(cartItem)
                PriceText(cartItem)
            }
            Row {
                DescriptionText(cartItem)
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FullView(
    cartItem: CartItem,
    addAction: (Item) -> Unit,
    toggleCheckAction: (CartItemProperties) -> Unit,
    subtractAction: (ItemId) -> Unit = {},
    beginEditMode: () -> Unit,
) {
    Spacer(Modifier.height(3.dp))
    val (cartItemProperties, item) = cartItem
    val alpha = if (cartItemProperties.checked) .7F else 1F
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .absolutePadding(left = 4.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (cartItemProperties.amount > 0) toggleCheckAction(cartItemProperties)
                },
                onLongClick = beginEditMode
            )
            .alpha(alpha)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            KategoryIndicator(cartItem.item)
            Spacer(Modifier.width(3.dp))
            Column() {
                InfoText(cartItem)
                PriceText(cartItem)
                DescriptionText(cartItem)
                Spacer(Modifier.height(3.dp))
            }
        }
        Buttons(cartItem, subtractAction, item, cartItemProperties, addAction)
    }
}

@Composable
fun PriceText(cartItem: CartItem) {
    Text(cartItem.item.price.toString() + " € ")
}

@Composable
private fun DescriptionText(cartItem: CartItem) {
    if (!cartItem.item.description.isNullOrBlank()) {
        Text(text = cartItem.item.description, color = Color.DarkGray)
    }
}

@Composable
private fun InfoText(
    cartItem: CartItem,
) {
    Row {
        if (cartItem.cartItemProperties.checked) {
            Icon(Icons.Outlined.CheckCircle, contentDescription = "Gekauft")
        }
        if (0 >= cartItem.cartItemProperties.amount) {
            Text(cartItem.item.name)
        } else {
            Text(cartItem.cartItemProperties.amount.toString() + " x " + cartItem.item.name)
        }
    }
}

@Composable
private fun Buttons(
    cartItem: CartItem,
    subtractAction: (ItemId) -> Unit,
    item: Item,
    cartItemProperties: CartItemProperties,
    addAction: (Item) -> Unit,
) {
    Row {
        if (cartItem.cartItemProperties.amount > 0) {
            Button(
                modifier = Modifier.height(35.dp),
                onClick = { subtractAction(item.itemId) },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = buttonColor(
                        cartItemProperties
                    )
                ),
            ) {
                Icon(Icons.Outlined.RemoveShoppingCart, contentDescription = "Löschen")
            }
            Spacer(modifier = Modifier.width(1.dp))
        }
        Button(
            modifier = Modifier.height(35.dp),
            shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp),
            onClick = { addAction(item) },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = buttonColor(
                    cartItemProperties
                )
            ),
        ) {
            Icon(Icons.Filled.AddShoppingCart, contentDescription = "Hinzufügen")
            Text(text = amountText(cartItemProperties).trim())
        }
    }
}

@Composable
private fun buttonColor(cartItemProperties: CartItemProperties) =
    if (0 == cartItemProperties.amount) MaterialTheme.colors.primaryVariant else MaterialTheme.colors.primary
