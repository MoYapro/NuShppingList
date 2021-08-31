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
import de.moyapro.nushppinglist.Item
import de.moyapro.nushppinglist.VM

@Composable
@Preview
fun ItemList(@PreviewParameter(ItemListProvider::class) viewModel: VM) {
    val items: List<Item> by viewModel.allItems.collectAsState(listOf())

    Column(Modifier.background(color = Color.Blue)) {
        items.forEach { item ->
            ItemListElement(
                item,
                viewModel.getCartItemPropertiesByItemId(item.itemId),
                viewModel::update,
                viewModel::addToCart
            )
        }
    }
}