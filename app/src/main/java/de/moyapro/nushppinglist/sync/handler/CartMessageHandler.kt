package de.moyapro.nushppinglist.sync.handler

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

    override suspend fun invoke(cartMessage: CartMessage) {
        cartMessage.cartItemPropertiesList.forEach { cartItemProperties ->
            val item = cartDao.getItemByItemId(cartItemProperties.itemId)
            if (null != item) {
                this.add(CartItem(cartItemProperties, item))
            } else {
                publisher.publish(RequestItemMessage(cartItemProperties.itemId))
                Thread.sleep(500) // wait for requested items to arrive
                invoke(cartMessage)
            }
        }
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
