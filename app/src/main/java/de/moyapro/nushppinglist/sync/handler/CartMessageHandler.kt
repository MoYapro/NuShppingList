package de.moyapro.nushppinglist.sync.handler

import android.util.Log
import de.moyapro.nushppinglist.constants.SWITCHES
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
        handleCartMessage(cartMessage)
        Log.i(tag, "done handle $cartMessage")
    }

    private suspend fun handleCartMessage(
        cartMessage: CartMessage,
        endTime: Long = System.currentTimeMillis() + SWITCHES.WAIT_FOR_MISSING_ITEMS_TIMEOUT,
    ) {
        val missingItemIds = cartMessage.cartItemPropertiesList
            .mapNotNull { cartItemProperties ->
                val item = cartDao.getItemByItemId(cartItemProperties.itemId)
                if (null != item) {
                    Log.i(tag, "save new cartItem for existing $item")
                    this.add(CartItem(cartItemProperties, item))
                    null
                } else {
                    cartItemProperties.itemId
                }
            }
        if (missingItemIds.isNotEmpty()) {
            Log.i(tag, "request non existing item with itemId $missingItemIds")
            publisher.publish(RequestItemMessage(missingItemIds))
            Log.i(tag, "wait and retry creating cart")
            Thread.sleep(1000) // wait for requested items to arrive
            if (System.currentTimeMillis() < endTime) handleCartMessage(cartMessage, endTime)
        }
    }

    private suspend fun add(newCartItem: CartItem) {
        println("vvv\tCartItem\t $newCartItem")
        val existingItem = cartDao.getItemByItemId(newCartItem.item.itemId)
        if (null == existingItem) {
            cartDao.save(newCartItem.item)
        } else {
            cartDao.updateAll(newCartItem.item)
        }
        val existingCartItemProperties = cartDao.getCartItemByItemId(newCartItem.item.itemId)
        if (null == existingCartItemProperties) {
            cartDao.save(newCartItem.cartItemProperties)
        } else {
            cartDao.updateAll(newCartItem.cartItemProperties)
        }
    }
}
