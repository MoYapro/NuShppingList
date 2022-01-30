package de.moyapro.nushppinglist.sync.handler

import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.dao.findAllSelectedCartItems
import de.moyapro.nushppinglist.db.ids.CartId
import de.moyapro.nushppinglist.db.model.Cart
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.messages.CartMessage
import de.moyapro.nushppinglist.sync.messages.RequestCartMessage
import kotlinx.coroutines.flow.toList

class RequestCartMessageHandler(
    val cartDao: CartDao,
    val publisher: Publisher,
) : suspend (RequestCartMessage) -> Unit {

    override suspend fun invoke(requestCartMessage: RequestCartMessage) {
        if (null == requestCartMessage.cartId) {
            publishAllSyncedCarts()
        } else {
            publishSingleCart(requestCartMessage.cartId)
        }
    }

    private suspend fun publishSingleCart(cartId: CartId) {
        val cartItemPropertiesList = cartDao.findAllSelectedCartItems(cartId)
            .toList()
            .flatten()
            .map(CartItem::cartItemProperties)
        publisher.publish(CartMessage(cartItemPropertiesList, cartId))
    }

    private suspend fun publishAllSyncedCarts() {
        cartDao.findAllCart()
            .toList()
            .flatten()
            .filter(Cart::synced)
            .map(Cart::cartId)
            .map { publishSingleCart(it) }
    }
}
