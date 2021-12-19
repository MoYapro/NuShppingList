package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.CartItemProperties
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.util.ItemListProvider

@Composable
@Preview
fun ItemList(@PreviewParameter(ItemListProvider::class) viewModel: CartViewModel) {
    val allItemList: List<Item> by viewModel.allItems.collectAsState(listOf())
    val cartItems: List<CartItem> by viewModel.allCartItems.collectAsState(
        listOf()
    )

    val cartItemList: List<CartItem> = allItemList.map { item ->
        val cartItem = cartItems.firstOrNull { it.item.itemId == item.itemId }
        cartItem
            ?: CartItem(
                CartItemProperties(
                    newItemId = item.itemId,
                    amount = 0
                ),
                item,
            )
    }

    Column {
        cartItemList.forEach { cartItem ->
            ItemListElement(
                cartItem,
                viewModel::update,
                viewModel::addToCart
            )
        }
    }
}
