package de.moyapro.nushppinglist.sync.handler

import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.dao.getAllItemByItemId
import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.messages.ItemMessage
import de.moyapro.nushppinglist.sync.messages.RequestItemMessage

class RequestItemMessageHandler(
    val cartDao: CartDao,
    val publisher: Publisher,
) :suspend (String) -> Unit {

    override suspend fun invoke(messageString: String) {
        val requestItemMessage: RequestItemMessage = RequestItemMessage(emptyList())
        val item = cartDao.getAllItemByItemId(requestItemMessage.itemIds)
        if (item.isEmpty()) {
            println("^^^\t item not found for itemId ${requestItemMessage.itemIds}")
        } else {
            publisher.publish(ItemMessage(item))
        }
    }
}
