package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import de.moyapro.nushppinglist.CartItemProperties
import de.moyapro.nushppinglist.VM

@Composable
fun CartListElement(
    cartItem: CartItemProperties,
    viewModel: VM
) {
    val item = viewModel.getItemByItemId(cartItem.itemId)
    if (null != item) {
        Row(
            Modifier.fillMaxWidth(),
        ) {
            Text(
                text = item.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { viewModel.toggleChecked(cartItem) })
                    .background(getBackgroundColor(cartItem.checked))
            )
        }
    } else {
        Text(text = "Unbekanntes Dings")
    }
}

@Composable
private fun getBackgroundColor(checked: Boolean) = when (checked) {
    true -> Color.Green
    false -> Color.Unspecified
}