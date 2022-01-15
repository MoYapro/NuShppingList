package de.moyapro.nushppinglist.sync.handler

import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.dao.getItemByItemId
import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.messages.ItemMessage

class ItemMessageHandler(
    val cartDao: CartDao,
    val publisher: Publisher,
) : suspend (ItemMessage) -> Unit {

    override suspend fun invoke(itemMessage: ItemMessage) {
        val itemInDB = cartDao.getItemByItemId(itemMessage.item.itemId)
        when {
            null == itemInDB -> cartDao.save(itemMessage.item)
            itemInDB == itemMessage.item -> return

            else -> throw IllegalStateException("don't know what to do when item id $itemInDB and itemMessage is $itemMessage")
        }

    }
}
