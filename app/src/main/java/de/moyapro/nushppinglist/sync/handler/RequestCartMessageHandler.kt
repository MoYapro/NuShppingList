package de.moyapro.nushppinglist.sync.handler

import android.util.Log
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.dao.findAllSelectedCartItems
import de.moyapro.nushppinglist.db.ids.CartId
import de.moyapro.nushppinglist.db.model.Cart
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.messages.CartMessage
import de.moyapro.nushppinglist.sync.messages.RequestCartMessage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList

class RequestCartMessageHandler(
    val cartDao: CartDao,
    val publisher: Publisher,
) : suspend (RequestCartMessage) -> Unit {

    val tag = RequestCartMessageHandler::class.simpleName

    override suspend fun invoke(requestCartMessage: RequestCartMessage) {
        if (null == requestCartMessage.cartId) {
            Log.i(tag, "syncing all carts")
            publishAllSyncedCarts()
        } else {
            Log.i(tag, "syncing cart ${requestCartMessage.cartId}")
            publishSingleCart(requestCartMessage.cartId)
        }
    }

    private suspend fun publishSingleCart(cartId: CartId) {
        Log.i(tag, "publish single cart for $cartId")
        val toList = cartDao.findAllSelectedCartItems(cartId)
        Log.i(tag, "got items to publish $toList")
        val first = toList.take(1).first()
        val cartItemPropertiesList = first.map(CartItem::cartItemProperties)
        Log.i(tag, "Syncing $cartItemPropertiesList for $cartId")

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
