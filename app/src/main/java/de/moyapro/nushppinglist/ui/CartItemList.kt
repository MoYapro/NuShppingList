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
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.ui.model.CartViewModel

@Composable
fun CartListElement(
    cartItem: CartItem,
    viewModel: CartViewModel,
) {
    Row(
        Modifier
            .fillMaxWidth(),
    ) {
        Spacer(modifier = Modifier.width(Dp(4F)))
        Text(text = cartItem.cartItemProperties.amount.toString())
        Spacer(modifier = Modifier.width(Dp(4F)))
        Text(text = cartItem.item.defaultItemUnit.short)
        Spacer(modifier = Modifier.width(Dp(4F)))
        Text(
            text = cartItem.item.name,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { viewModel.toggleChecked(cartItem.cartItemProperties) })
                .background(getBackgroundColor(cartItem.cartItemProperties.checked))
        )
    }
}

@Composable
private fun getBackgroundColor(checked: Boolean) = when (checked) {
    true -> Color.LightGray
    false -> Color.Unspecified
}
