package de.moyapro.nushppinglist.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import de.moyapro.nushppinglist.Item

class ItemProvider : PreviewParameterProvider<Item> {
    override val values: Sequence<Item>
        get() = listOf(Item("Milk")).asSequence()

}

class ItemListProvider : PreviewParameterProvider<List<Item>> {
    override val values: Sequence<List<Item>>
        get() = listOf(listOf(Item("Milk"), Item("Apple"))).asSequence()

}