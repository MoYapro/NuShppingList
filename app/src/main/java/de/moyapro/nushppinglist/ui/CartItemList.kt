package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import de.moyapro.nushppinglist.CartItemProperties
import de.moyapro.nushppinglist.VM

@Composable
fun CartListElement(
    cartItem: CartItemProperties,
    viewModel: VM
) {
    val item = viewModel.getItemByItemId(cartItem.itemId)
    if (null != item) {
        Row {
            Text(text = item.name)
        }
    } else {
        Text(text = "Unbekanntes Dings")
    }
}