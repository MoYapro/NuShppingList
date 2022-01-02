package de.moyapro.nushppinglist.sync.handler

import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.messages.CartMessage
import de.moyapro.nushppinglist.ui.model.CartViewModel

class CartMessageHandler(
    val viewModel: CartViewModel,
    val publisher: Publisher,
) : (CartMessage) -> Unit {

    override fun invoke(requestItemMessage: CartMessage) {

    }
}
