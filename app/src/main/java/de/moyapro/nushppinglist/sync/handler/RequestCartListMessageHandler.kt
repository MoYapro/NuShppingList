package de.moyapro.nushppinglist.sync.handler

import android.util.Log
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.model.Cart
import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.messages.CartListMessage
import de.moyapro.nushppinglist.sync.messages.RequestCartListMessage

class RequestCartListMessageHandler(
    val cartDao: CartDao,
    val publisher: Publisher,
) :suspend (RequestCartListMessage) -> Unit {

    private val tag = RequestCartListMessageHandler::class.simpleName

    override suspend fun invoke(requestCartlistMessage: RequestCartListMessage) {
        Log.d(tag, "start handling $requestCartlistMessage")
        val syncedCarts: List<Cart> = cartDao.getSyncedCarts()
        Log.d(tag, "found carts to sync: $syncedCarts")
        if (syncedCarts.isNotEmpty()) {
            publisher.publish(CartListMessage(syncedCarts))
        } else {
            Log.d(tag, "No synced carts found")
        }
    }
}
