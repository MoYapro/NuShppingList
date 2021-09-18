package de.moyapro.nushppinglist.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import de.moyapro.nushppinglist.Item

class LabelProvider : PreviewParameterProvider<String> {
    override val values: Sequence<String>
        get() = listOf("The Label").asSequence()
}


class ItemListProvider : PreviewParameterProvider<List<Item>> {
    override val values: Sequence<List<Item>>
        get() = listOf(listOf(Item("Milk"), Item("Apple"))).asSequence()

}
