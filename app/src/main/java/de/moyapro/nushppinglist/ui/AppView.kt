package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import de.moyapro.nushppinglist.VM

@Composable
fun AppView(viewModel: VM, showCart: Boolean = true) {
    var cartIsDisplayed: Boolean by remember { mutableStateOf(showCart) }
    Column {

        Button(onClick = {
            cartIsDisplayed = !cartIsDisplayed
        }) {
            Text("XXX")
        }

        if (cartIsDisplayed)
            CartView(viewModel)
        else
            ItemView(viewModel)
    }
}