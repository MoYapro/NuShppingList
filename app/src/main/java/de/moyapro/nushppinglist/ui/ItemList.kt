package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import de.moyapro.nushppinglist.Item

@Composable
@Preview
fun ItemList(@PreviewParameter(ItemListProvider::class) items: List<Item>) {
    Column {
        items.forEach { item ->
            ItemListElement(item)
        }
    }
}