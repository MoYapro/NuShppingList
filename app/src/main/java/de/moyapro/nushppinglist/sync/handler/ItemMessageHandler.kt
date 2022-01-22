package de.moyapro.nushppinglist.sync.handler

import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.dao.getItemByItemId
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.messages.ItemMessage

class ItemMessageHandler(
    val cartDao: CartDao,
    val publisher: Publisher,
) : suspend (ItemMessage) -> Unit {

    override suspend fun invoke(itemMessage: ItemMessage) {
        itemMessage.items.forEach{ itemFromMessage ->
        val itemInDB = cartDao.getItemByItemId(itemFromMessage.itemId)
        val itemWithSameName = cartDao.getItemByItemName(itemFromMessage.name)
        when {
            itemInDB == itemFromMessage -> return
            null == itemInDB -> cartDao.save(itemFromMessage)
            null != itemWithSameName -> cartDao.updateAll(merge(itemFromMessage, itemWithSameName))
            null != itemInDB -> cartDao.updateAll(merge(itemInDB, itemFromMessage))
            else -> throw IllegalStateException("don't know what to do when itemInDb $itemInDB and itemMessage is $itemMessage")
        }
        }

    }

    fun merge(newValues: Item, originalItem: Item): Item {
        val default = Item()
        return Item(
            itemId = originalItem.itemId,
            name = takeIfNotDefault(originalItem, default, newValues) { it.name },
            description = takeIfNotDefault(originalItem, default, newValues) { it.description },
            defaultItemAmount = takeIfNotDefault(originalItem, default, newValues) { it.defaultItemAmount },
            defaultItemUnit = takeIfNotDefault(originalItem, default, newValues) { it.defaultItemUnit },
            price = takeIfNotDefault(originalItem, default, newValues) { it.price },
            kategory = takeIfNotDefault(originalItem, default, newValues) { it.kategory }

        )
    }

    fun <T, X : Any> takeIfNotDefault(
        input: T,
        default: T,
        alternative: T,
        fieldAccessor: (T) -> X,
    ): X {
        return if (fieldAccessor(input) != fieldAccessor(default)) {
            fieldAccessor(input)
        } else {
            fieldAccessor(alternative)
        }
    }

}
