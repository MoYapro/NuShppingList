package de.moyapro.nushppinglist.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SyncDisabled
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.moyapro.nushppinglist.constants.SWITCHES
import de.moyapro.nushppinglist.db.model.Cart
import de.moyapro.nushppinglist.ui.component.EditTextField
import de.moyapro.nushppinglist.ui.component.HoldableButton

@Composable
fun CartListElement(
    cart: Cart,
    saveAction: (Cart) -> Unit = {},
    deleteAction: (Cart) -> Unit = {},
) {
    var isEdited: Boolean by remember { mutableStateOf(false) }

    Column(Modifier
        .fillMaxWidth()
    ) {
        if (SWITCHES.DEBUG) {
            Text(cart.cartId.toString())
        }
        Surface(
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
            ) {
                if (isEdited) {
                    EditView(
                        cart,
                        saveAction,
                        deleteAction
                    ) { isEdited = false }
                } else {
                    JustView(
                        cart = cart,
                        beginEditMode = { isEdited = true },
                    )
                }
            }
        }
    }
}


@Composable
fun EditView(
    cart: Cart,
    saveAction: (Cart) -> Unit,
    deleteAction: (Cart) -> Unit,
    endEditMode: () -> Unit,
) {
    var editCart by remember { mutableStateOf(cart) }
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val saveActionClosure = {
            endEditMode()
            saveAction(editCart)
        }
        Column(Modifier.fillMaxWidth()) {
            EditTextField(
                label = "Name",
                initialValue = editCart.cartName,
                onValueChange = { editCart = editCart.copy(cartName = it) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                doneAction = saveActionClosure
            )
            Spacer(modifier = Modifier.height(Dp(8F)))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row {
                    Icon(Icons.Filled.Sync, contentDescription = "Neu")
                    Text(" / ")
                    Icon(Icons.Filled.SyncDisabled, contentDescription = "Neu")
                }
                Switch(checked = editCart.synced,
                    onCheckedChange = { editCart = editCart.copy(synced = it) })
            }
            Spacer(modifier = Modifier.height(Dp(8F)))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .absolutePadding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HoldableButton(
                    onLongPress = {
                        endEditMode()
                        deleteAction(editCart)
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

@Composable
fun JustView(
    cart: Cart,
    beginEditMode: () -> Unit,
) {
    Spacer(Modifier.height(3.dp))
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .absolutePadding(left = 4.dp)
            .fillMaxWidth()
            .clickable(onClick = beginEditMode)
    ) {
        Text(cart.cartName)
        Spacer(modifier = Modifier.width(2.dp))
        if (cart.synced)
            Icon(Icons.Filled.Sync, contentDescription = "private")
        else
            Icon(Icons.Filled.SyncDisabled, contentDescription = "private")

    }
    Spacer(Modifier.height(3.dp))
}
