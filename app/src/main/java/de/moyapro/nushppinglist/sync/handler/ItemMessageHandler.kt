package de.moyapro.nushppinglist.sync.handler

import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.dao.getItemByItemId
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.messages.ItemMessage
import de.moyapro.nushppinglist.util.takeIfNotDefault

class ItemMessageHandler(
    val cartDao: CartDao,
    val publisher: Publisher?,
) : suspend (ItemMessage) -> Unit {

    override suspend fun invoke(itemMessage: ItemMessage) {
        itemMessage.items.forEach { itemFromMessage ->
            val itemInDb = cartDao.getItemByItemId(itemFromMessage.itemId)
            val itemWithSameName = cartDao.getItemByItemName(itemFromMessage.name)
            when {
                itemInDb == itemFromMessage -> return
                null == itemInDb && null == itemWithSameName -> cartDao.save(itemFromMessage)
                null == itemInDb && null != itemWithSameName -> cartDao.updateAll(
                    merge(itemWithSameName, itemFromMessage))
                null != itemInDb && null == itemWithSameName -> cartDao.updateAll(
                    merge(itemInDb, itemFromMessage))
                null != itemInDb && null != itemWithSameName -> {
                    cartDao.remove(itemInDb)
                    cartDao.updateAll(merge(merge(itemInDb, itemWithSameName), itemFromMessage))
                }
            }
        }

    }

    fun merge(originalItem: Item, newValues: Item): Item {
        val default = Item()
        return Item(
            itemId = originalItem.itemId,
            name = takeIfNotDefault(originalItem, default, newValues) { it.name },
            description = takeIfNotDefault(originalItem, default, newValues) { it.description },
            defaultItemAmount = takeIfNotDefault(originalItem,
                default,
                newValues) { it.defaultItemAmount },
            defaultItemUnit = takeIfNotDefault(originalItem,
                default,
                newValues) { it.defaultItemUnit },
            price = takeIfNotDefault(originalItem, default, newValues) { it.price },
            kategory = takeIfNotDefault(originalItem, default, newValues) { it.kategory }

        )
    }

}
