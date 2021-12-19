package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import de.moyapro.nushppinglist.db.model.CartItemProperties
import de.moyapro.nushppinglist.ui.model.CartViewModel

@Composable
fun CartListElement(
    cartItem: CartItemProperties,
    viewModel: CartViewModel
) {
    val item = viewModel.getItemByItemId(cartItem.itemId)
    if (null != item) {
        Row(
            Modifier
                .background(color = Color.DarkGray)
                .fillMaxWidth(),
        ) {
            Spacer(modifier = Modifier.width(Dp(4F)))
            Text(text = cartItem.amount.toString())
            Spacer(modifier = Modifier.width(Dp(4F)))
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
    true -> Color.LightGray
    false -> Color.Unspecified
}
