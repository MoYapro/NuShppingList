package de.moyapro.nushppinglist.ui

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
    val chooseAction: (String) -> Unit = { selectedValue -> viewModel.addToCart(selectedValue) }
    val autocompleteAction: (String) -> List<String> = { searchString ->
        viewModel.getAutocompleteItems(searchString)
    }


    cartItemProperties.forEach { item ->
        CartListElement(item, viewModel)
    }
    Autocomplete(chooseAction, autocompleteAction)
}




