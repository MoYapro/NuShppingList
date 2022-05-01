package de.moyapro.nushppinglist.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import de.moyapro.nushppinglist.constants.CONSTANTS.DEFAULT_CART_NAME
import de.moyapro.nushppinglist.constants.SWITCHES
import de.moyapro.nushppinglist.db.model.Cart
import de.moyapro.nushppinglist.ui.model.CartViewModel

@Composable
fun CartSelector(viewModel: CartViewModel) {
    val carts: List<Cart?> by viewModel.allCart.collectAsState(listOf())
    val cartsAndEmpty = listOf(null) + carts


    val selectedCart: Cart? by viewModel.selectedCart.collectAsState(null)
    if (carts.isNullOrEmpty()) {
        return
    }
    Column() {

        Dropdown(
            label = DEFAULT_CART_NAME,
            initialValue = selectedCart,
            values = cartsAndEmpty,
            onValueChange = {
                viewModel.selectCart(it)
            },
            itemLabel = { it?.cartName ?: DEFAULT_CART_NAME },
            modifier = Modifier.fillMaxWidth()
        )
        if (SWITCHES.DEBUG) {
            Text(selectedCart.toString())
        }
    }
}
