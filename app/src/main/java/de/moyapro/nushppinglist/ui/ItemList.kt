package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import de.moyapro.nushppinglist.Item

@Composable
fun ItemList(items: List<Item>) {
    Column {
        items.forEach { item ->
            ItemListElement(item)
        }
    }
}