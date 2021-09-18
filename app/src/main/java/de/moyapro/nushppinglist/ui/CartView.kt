package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import de.moyapro.nushppinglist.VM
import de.moyapro.nushppinglist.db.model.CartItemProperties


@Composable
fun CartView(viewModel: VM) {
    val cartItemProperties: List<CartItemProperties> by viewModel.cartItems.collectAsState(
        listOf()
    )
    val chooseAction: (String) -> Unit = viewModel::addToCart
    val autocompleteAction: (String) -> List<String> = { searchString ->
        viewModel.getAutocompleteItems(searchString)
    }
    Column(Modifier.background(color = Color.Green)) {
        Button(onClick = { viewModel.removeCheckedFromCart() }) {
            Text("⎚")
        }
        cartItemProperties.forEach { item ->
            CartListElement(item, viewModel)
        }
        Autocomplete(chooseAction, autocompleteAction)
    }
}




