package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import de.moyapro.nushppinglist.VM
import de.moyapro.nushppinglist.ui.theme.Purple700

@Composable
fun AppView(viewModel: VM, showCart: Boolean = true) {
    var cartIsDisplayed: Boolean by remember { mutableStateOf(showCart) }
    Column {

        cartIsDisplayed = ViewSelector(cartIsDisplayed) { newValue -> cartIsDisplayed = newValue }

        if (cartIsDisplayed)
            CartView(viewModel)
        else
            ItemView(viewModel)
    }
}

@Composable
private fun ViewSelector(cartIsDisplayed: Boolean, setNewState: (Boolean) -> Unit): Boolean {
    var cartIsDisplayed1 = cartIsDisplayed
    Row {
        Button(
            modifier = Modifier
                .fillMaxWidth(.5F),
            colors = ButtonDefaults.buttonColors(backgroundColor = if (cartIsDisplayed1) Purple700 else Color.Gray),
            onClick = {
                setNewState(true)
            }) {
            Text("Einkaufsliste")
        }
        Spacer(modifier = Modifier.width(Dp(4F)))
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = if (!cartIsDisplayed1) Purple700 else Color.Gray),
            onClick = {
                setNewState(false)
            }) {
            Text("Dinge")
        }
    }
    return cartIsDisplayed1
}