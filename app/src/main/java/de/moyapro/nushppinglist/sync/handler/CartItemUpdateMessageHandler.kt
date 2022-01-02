package de.moyapro.nushppinglist.sync.handler

import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.messages.CartItemUpdateMessage
import de.moyapro.nushppinglist.ui.model.CartViewModel

class CartItemUpdateMessageHandler(
    val viewModel: CartViewModel,
    val publisher: Publisher,
) : (CartItemUpdateMessage) -> Unit {

    override fun invoke(cartItemUpdateMessage: CartItemUpdateMessage) {
        viewModel.update(cartItemUpdateMessage.cartItemProperties)
    }
}
