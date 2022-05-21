package de.moyapro.nushppinglist.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import de.moyapro.nushppinglist.constants.CONSTANTS.DEFAULT_CART
import de.moyapro.nushppinglist.constants.SWITCHES
import de.moyapro.nushppinglist.db.model.Cart
import de.moyapro.nushppinglist.ui.model.CartViewModel

@Composable
fun CartSelector(viewModel: CartViewModel) {
    val carts: List<Cart> by viewModel.allCart.collectAsState(listOf())


    val selectedCart: Cart? by viewModel.selectedCart.collectAsState(DEFAULT_CART)
    if (carts.isNullOrEmpty()) {
        return
    }
    Column() {

        Dropdown(
            label = "WTF GOES HERE",
            initialValue = selectedCart,
            values = carts,
            onValueChange = {
                viewModel.selectCart(it)
            },
            itemLabel = { it?.cartName ?: DEFAULT_CART.cartName},
            modifier = Modifier.fillMaxWidth()
        )
        if (SWITCHES.DEBUG) {
            Text(selectedCart.toString())
        }
    }
}
