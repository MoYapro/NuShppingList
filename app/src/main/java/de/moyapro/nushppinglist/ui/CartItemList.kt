package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.CartItemProperties
import de.moyapro.nushppinglist.ui.component.KategoryIndicator
import java.math.BigDecimal

@Composable
fun CartListElement(
    cartItem: CartItem,
    toggleCheckedAction: (CartItemProperties) -> Unit,
) {
    val backgroundColor = getBackgroundColor(checked = cartItem.cartItemProperties.checked)
    val textColor = contentColorFor(backgroundColor)
    val alpha = if (cartItem.cartItemProperties.checked) .7F else 1F
    val rowTotal = cartItem.item.price * BigDecimal(cartItem.cartItemProperties.amount)

    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = {toggleCheckedAction(cartItem.cartItemProperties)})
            .background(backgroundColor)
            .alpha(alpha),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.fillMaxWidth(.8F)) {
            KategoryIndicator(cartItem.item)
            Spacer(modifier = Modifier.width(Dp(4F)))
            Text(text = cartItem.cartItemProperties.amount.toString(), color = textColor)
            Spacer(modifier = Modifier.width(Dp(4F)))
            Text(text = cartItem.item.defaultItemUnit.short, color = textColor)
            Spacer(modifier = Modifier.width(Dp(4F)))
            if (cartItem.cartItemProperties.checked) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = "Gekauft")
                Spacer(modifier = Modifier.width(Dp(4F)))
            }
            Column {
                Text(text = cartItem.item.name, color = textColor)
                if (cartItem.item.description.isNotBlank()) {
                    Text(
                        text = cartItem.item.description,
                        fontSize = 13.sp,
                        color = textColor,
                        modifier = Modifier.alpha(CONSTANTS.MUTED_ALPHA)
                    )
                }
            }
        }
        Row {
            Text(text = "$rowTotal €", color = textColor)
        }
        Spacer(modifier = Modifier.width(Dp(4F)))
    }
}

@Composable
private fun getBackgroundColor(checked: Boolean) = when (checked) {
    true -> MaterialTheme.colors.surface
    false -> Color.Unspecified
}
