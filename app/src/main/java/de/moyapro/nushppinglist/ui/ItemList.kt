package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.CartItemProperties
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.ui.component.EditTextField
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.util.ItemListProvider

@Composable
@Preview
fun ItemList(@PreviewParameter(ItemListProvider::class) viewModel: CartViewModel) {
    val allItemList: List<Item> by viewModel.allItems.collectAsState(listOf())
    val cartItems: List<CartItem> by viewModel.allCartItems.collectAsState(
        listOf()
    )

    var filter: String by remember { mutableStateOf("") }

    val cartItemList: List<CartItem> = allItemList
        .filter { it.name.lowercase().contains(filter.lowercase()) }
        .map { item ->
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

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.add(Item("?")) }) {
                Icon(Icons.Filled.Add, contentDescription = "Hinzufügen")
            }
        },
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(count = cartItemList.size) { index ->
                    val cartItem = cartItemList[index]
                    ItemListElement(
                        cartItem,
                        viewModel::update,
                        viewModel::addToCart
                    )
                }
            }
        },
        bottomBar = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                EditTextField(initialValue = filter, onValueChange = { filter = it }, widthPercentage = .8F)
                Button(onClick = { filter = "" }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Leeren")
                }
            }
        }
    )
}
