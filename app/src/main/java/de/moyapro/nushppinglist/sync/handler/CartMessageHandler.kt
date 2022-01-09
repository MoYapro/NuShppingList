package de.moyapro.nushppinglist.sync.handler

import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.messages.CartMessage
import de.moyapro.nushppinglist.sync.messages.RequestItemMessage
import de.moyapro.nushppinglist.ui.model.CartViewModel

class CartMessageHandler(
    val viewModel: CartViewModel,
    val publisher: Publisher,
) : (CartMessage) -> Unit {

    override fun invoke(cartMessage: CartMessage) {
        cartMessage.cartItemPropertiesList.forEach { cartItemProperties ->
            val item = viewModel.getItemByItemId(cartItemProperties.itemId)
            if (null != item) {
                viewModel.add(CartItem(cartItemProperties, item))
            } else {
                publisher.publish(RequestItemMessage(cartItemProperties.itemId))
                Thread.sleep(500) // wait for requested items to arrive
                invoke(cartMessage)
            }
        }
    }
}
