package de.moyapro.nushppinglist.sync.handler

import android.util.Log
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.dao.getAllItemByItemId
import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.messages.ItemMessage
import de.moyapro.nushppinglist.sync.messages.RequestItemMessage

class RequestItemMessageHandler(
    val cartDao: CartDao,
    val publisher: Publisher,
) :suspend (RequestItemMessage) -> Unit {
val tag = RequestItemMessageHandler::class.simpleName
    override suspend fun invoke(requestItemMessage: RequestItemMessage) {
        val item = cartDao.getAllItemByItemId(requestItemMessage.itemIds)
        if (item.isEmpty()) {
            Log.d(tag, "^^^\t item not found for itemId ${requestItemMessage.itemIds}")
        } else {
            publisher.publish(ItemMessage(item))
        }
    }
}
