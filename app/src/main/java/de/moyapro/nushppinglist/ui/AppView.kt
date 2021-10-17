package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.theme.Purple700

@Composable
fun AppView(viewModel: CartViewModel, showCart: Boolean = true) {
    var cartIsDisplayed: Boolean by remember { mutableStateOf(showCart) }
    Column(Modifier.background(color = Color.Magenta)) {

        ViewSelector(cartIsDisplayed) { newValue -> cartIsDisplayed = newValue }

        if (cartIsDisplayed)
            CartView(viewModel)
        else
            ItemView(viewModel)
    }
}

@Composable
private fun ViewSelector(cartIsDisplayed: Boolean, setNewState: (Boolean) -> Unit) {
    Row {
        Button(
            modifier = Modifier
                .fillMaxWidth(.5F),
            colors = ButtonDefaults.buttonColors(backgroundColor = if (cartIsDisplayed) Purple700 else Color.Gray),
            onClick = {
                setNewState(true)
            }) {
            Text("Einkaufsliste")
        }
        Spacer(modifier = Modifier.width(Dp(4F)))
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = if (!cartIsDisplayed) Purple700 else Color.Gray),
            onClick = {
                setNewState(false)
            }) {
            Text("Dinge")
        }
    }
}
