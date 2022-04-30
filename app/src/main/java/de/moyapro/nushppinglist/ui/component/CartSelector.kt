package de.moyapro.nushppinglist.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import de.moyapro.nushppinglist.constants.SWITCHES
import de.moyapro.nushppinglist.db.model.Cart
import de.moyapro.nushppinglist.ui.model.CartViewModel

@Composable
fun CartSelector(viewModel: CartViewModel) {
    val carts: List<Cart?> by viewModel.allCart.collectAsState(listOf())
    val cartsAndEmpty = listOf(null) + carts


    var selectedCart: Cart? by remember { mutableStateOf(viewModel.selectedCart) }
    if (carts.isNullOrEmpty()) {
        return
    }
    Column() {

        Dropdown(
            label = "Alle Listen",
            initialValue = selectedCart,
            values = cartsAndEmpty,
            onValueChange = {
                selectedCart = it
                viewModel.selectCart(it)
            },
            itemLabel = { it?.cartName ?: "Alle Listen" },
            modifier = Modifier.fillMaxWidth()
        )
        if (SWITCHES.DEBUG) {
            Text(selectedCart.toString())
        }
    }
}
