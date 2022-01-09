package de.moyapro.nushppinglist.sync.handler

import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.dao.getItemByItemId
import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.messages.ItemMessage
import de.moyapro.nushppinglist.sync.messages.RequestItemMessage

class RequestItemMessageHandler(
    val cartDao: CartDao,
    val publisher: Publisher,
) :suspend (RequestItemMessage) -> Unit {

    override suspend fun invoke(requestItemMessage: RequestItemMessage) {
        val item = cartDao.getItemByItemId(requestItemMessage.itemId)
        if (null != item) {
            publisher.publish(ItemMessage(item))
        } else {
            println("^^^\t item not found for itemId ${requestItemMessage.itemId}")
        }
    }
}
