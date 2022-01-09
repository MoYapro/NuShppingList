package de.moyapro.nushppinglist.sync.handler

import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.messages.ItemMessage

class ItemMessageHandler(
    val cartDao: CartDao,
    val publisher: Publisher,
) : suspend (ItemMessage) -> Unit {

    override suspend fun invoke(itemMessage: ItemMessage) {
        cartDao.save(itemMessage.item)
    }
}
