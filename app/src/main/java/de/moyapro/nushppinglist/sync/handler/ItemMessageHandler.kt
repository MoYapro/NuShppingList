package de.moyapro.nushppinglist.sync.handler

import android.util.Log
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.dao.getItemByItemId
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.messages.ItemMessage
import de.moyapro.nushppinglist.util.takeIfNotDefault

class ItemMessageHandler(
    val cartDao: CartDao,
    val publisher: Publisher? = null,
) : suspend (ItemMessage) -> Unit {

    val tag = ItemMessageHandler::class.simpleName

    override suspend fun invoke(itemMessage: ItemMessage) {
        Log.d(tag, "handle itemMessage: $itemMessage")
        itemMessage.items.forEach { itemFromMessage ->
            val itemInDb = cartDao.getItemByItemId(itemFromMessage.itemId)
            val itemWithSameName = cartDao.getItemByItemName(itemFromMessage.name)
            Log.d(tag, "$itemFromMessage found in DB: $itemInDb")
            Log.d(tag, "$itemFromMessage found with same name: $itemWithSameName")
            when {
                itemInDb == itemFromMessage -> {
                    Log.d(tag, "Item already in DB")
                    return
                }
                null == itemInDb && null == itemWithSameName -> {
                    Log.d(tag, "+++\t$itemFromMessage")
                    cartDao.save(itemFromMessage)
                }
                null == itemInDb && null != itemWithSameName -> cartDao.updateAll(
                    merge(itemWithSameName, itemFromMessage))
                null != itemInDb && null == itemWithSameName -> cartDao.updateAll(
                    merge(itemInDb, itemFromMessage))
                null != itemInDb && null != itemWithSameName -> {
                    cartDao.updateAll(merge(merge(itemInDb, itemWithSameName), itemFromMessage))
                }
                else -> throw IllegalStateException("Could not determin action for item $itemFromMessage")
            }
        }

    }

    fun merge(originalItem: Item, newValues: Item): Item {
        Log.d(tag, "Merging $newValues ==> $originalItem")
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
