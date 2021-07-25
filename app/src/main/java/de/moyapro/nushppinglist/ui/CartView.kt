package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.runtime.*
import de.moyapro.nushppinglist.CartItem
import de.moyapro.nushppinglist.CartItemProperties
import de.moyapro.nushppinglist.VM


@Composable
fun CartView(viewModel: VM) {
    val cartItemProperties: List<CartItemProperties> by viewModel.cartItems.collectAsState(
        listOf()
    )
    var currentSearchText = remember { mutableStateOf("") }

    Column {
        cartItemProperties.forEach { item ->
            CartListElement(item, viewModel)
        }
        Row {
            EditTextField(
                initialValue = currentSearchText.value,
                onValueChange = { newText: String -> currentSearchText.value = newText.trim() })
            Button(onClick = {
                viewModel.add(CartItem(currentSearchText.value))
                currentSearchText.value = ""
            }) {
                Label(labelText = "+")
            }
        }

    }
}