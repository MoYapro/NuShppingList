package de.moyapro.nushppinglist.sync.handler

import android.util.Log
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.dao.getCartItemByItemId
import de.moyapro.nushppinglist.db.dao.getItemByItemId
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.messages.CartMessage
import de.moyapro.nushppinglist.sync.messages.RequestItemMessage

class CartMessageHandler(
    val cartDao: CartDao,
    val publisher: Publisher,
) : suspend (CartMessage) -> Unit {

    private val tag = CartMessageHandler::class.simpleName

    override suspend fun invoke(cartMessage: CartMessage) {
        Log.i(tag, "start handle $cartMessage")
        val didSaveAll = cartMessage.cartItemPropertiesList.map { cartItemProperties ->
            val item = cartDao.getItemByItemId(cartItemProperties.itemId)
            if (null != item) {
                Log.i(tag, "save new cartItem for existing $item")
                this.add(CartItem(cartItemProperties, item))
                true
            } else {
                Log.i(tag, "request non existing item with itemId ${cartItemProperties.itemId}")
                publisher.publish(RequestItemMessage(cartItemProperties.itemId))
                false
            }
        }.all { didSave -> didSave }
        if (!didSaveAll) {
            Log.i(tag, "wait and retry creating cart")
            Thread.sleep(500) // wait for requested items to arrive
            invoke(cartMessage)
        }
        Log.i(tag, "done handle $cartMessage")
    }

    private suspend fun add(newItem: CartItem) {
        println("vvv\tCartItem\t $newItem")
        val existingItem = cartDao.getItemByItemId(newItem.item.itemId)
        if (null == existingItem) {
            cartDao.save(newItem.item)
        } else {
            cartDao.updateAll(newItem.item)
        }
        val existingCartItemProperties = cartDao.getCartItemByItemId(newItem.item.itemId)
        if (null == existingCartItemProperties) {
            cartDao.save(newItem.cartItemProperties)
        } else {
            cartDao.updateAll(newItem.cartItemProperties)
        }
    }
}
