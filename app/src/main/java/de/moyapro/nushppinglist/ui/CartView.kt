package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import de.moyapro.nushppinglist.CartItemProperties
import de.moyapro.nushppinglist.VM


@Composable
fun CartView(viewModel: VM) {
    val cartItemProperties: List<CartItemProperties> by viewModel.cartItems.collectAsState(
        listOf()
    )
    Column {
        cartItemProperties.forEach { item ->
            CartListElement(item, viewModel)
        }
    }
}