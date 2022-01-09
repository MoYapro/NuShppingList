package de.moyapro.nushppinglist.sync.handler

import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.messages.CartMessage
import de.moyapro.nushppinglist.sync.messages.RequestCartMessage

class RequestCartMessageHandler(
    val cartDao: CartDao,
    val publisher: Publisher,
) : suspend (RequestCartMessage) -> Unit {

    override suspend fun invoke(requestCartMessage: RequestCartMessage) {
        publisher.publish(
            CartMessage(cartDao.getAllCartItems().map { it.cartItemProperties })
        )
    }
}
