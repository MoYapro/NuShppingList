package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import de.moyapro.nushppinglist.VM
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.CartItemProperties
import de.moyapro.nushppinglist.db.model.Item

@Composable
@Preview
fun ItemList(@PreviewParameter(ItemListProvider::class) viewModel: VM) {
    val allItemList: List<Item> by viewModel.allItems.collectAsState(listOf())
    val cartItemProperties: List<CartItemProperties> by viewModel.cartItems.collectAsState(
        listOf()
    )
    val cartItemList = allItemList.map { item ->
        CartItem(
            cartItemProperties.firstOrNull { item.itemId == it.cartItemId } ?: CartItemProperties(
                newItemId = item.itemId,
                amount = 0
            ),
            item,
        )
    }

    Column(Modifier.background(color = Color.Blue)) {
        cartItemList.forEach { cartItem ->
            ItemListElement(
                cartItem,
                viewModel::update,
                viewModel::addToCart
            )
        }
    }
}
