package de.moyapro.nushppinglist.sync.handler

import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.messages.ItemMessage
import de.moyapro.nushppinglist.ui.model.CartViewModel

class ItemMessageHandler(
    val viewModel: CartViewModel,
    val publisher: Publisher,
) : (ItemMessage) -> Unit {

    override fun invoke(itemMessage: ItemMessage) {
        viewModel.add(itemMessage.item)
    }
}
