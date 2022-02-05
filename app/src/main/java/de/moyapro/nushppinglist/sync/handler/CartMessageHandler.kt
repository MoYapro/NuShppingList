package de.moyapro.nushppinglist.sync.handler

import android.util.Log
import de.moyapro.nushppinglist.constants.SWITCHES
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.dao.getAllItemByItemId
import de.moyapro.nushppinglist.db.dao.getCartByCartId
import de.moyapro.nushppinglist.db.dao.getCartItemByItemId
import de.moyapro.nushppinglist.db.model.CartItemProperties
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.messages.CartMessage
import de.moyapro.nushppinglist.sync.messages.RequestCartListMessage
import de.moyapro.nushppinglist.sync.messages.RequestItemMessage
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

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
        if (System.currentTimeMillis() > endTime) {
            Log.w(tag,
                "Could not handle cart message $cartMessage after ${SWITCHES.WAIT_FOR_MISSING_ITEMS_TIMEOUT}ms")
            return
        }

        val requestedCartCount = requestMissingCarts(cartMessage)
        val requestedItemsCount = requestMissingItemIds(cartMessage)
        removeZeroAmountItems(cartMessage)
        if (0 < requestedItemsCount) {
            Log.i(tag, "Did not find items for #$requestedCartCount itemIds")
            tryAgainLater(cartMessage, endTime)
        } else {
            persistCartItemProperties(cartMessage)
        }

//            .mapNotNull { cartItemProperties ->
//                val item = cartDao.getItemByItemId(cartItemProperties.itemId)
//                if (null != item) {
//                    Log.i(tag, "save new cartItem for existing $item")
//                    if (0 >= cartItemProperties.amount) {
//                        this.remove(cartItemProperties)
//                    } else {
//                        this.add(CartItem(cartItemProperties, item))
//                    }
//                    null
//                } else {
//                    cartItemProperties.itemId
//                }
//            }

    }

    private suspend fun requestMissingCarts(cartMessage: CartMessage): Int {
        val existingCart = cartDao.getCartByCartId(cartMessage.cartId)
        return if (null == existingCart) {
            0
        } else {
            Log.i(tag, "request non existing cart with cartId ${cartMessage.cartId}")
            publisher.publish(RequestCartListMessage("Missing: ${listOf(cartMessage.cartId)}"))
            1
        }
    }

    private suspend fun persistCartItemProperties(cartMessage: CartMessage) {
        cartMessage.cartItemPropertiesList.forEach { add(it) }
    }

    private fun removeZeroAmountItems(cartMessage: CartMessage) {
        cartMessage.cartItemPropertiesList.filter { 0 >= it.amount }.forEach(::remove)
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun tryAgainLater(cartMessage: CartMessage, endTime: Long) {
        delay(1.seconds)
        handleCartMessage(cartMessage, endTime)
    }

    private suspend fun requestMissingItemIds(cartMessage: CartMessage): Int {
        val requestedItemIds = cartMessage.cartItemPropertiesList.map(CartItemProperties::itemId)
        val availableItemIds = cartDao.getAllItemByItemId(requestedItemIds).map(Item::itemId)
        val missingItemIds = requestedItemIds - availableItemIds

        if (missingItemIds.isNotEmpty()) {
            Log.i(tag, "request non existing item with itemId $missingItemIds")
            publisher.publish(RequestItemMessage(missingItemIds))
        }
        return missingItemIds.size
    }

    private fun remove(cartItemProperties: CartItemProperties) {
        println("---\tCartItemProperties: $cartItemProperties")
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            val existingCartItemProperties = cartDao.getCartItemByItemId(cartItemProperties.itemId,
                cartDao.getSelectedCart()?.cartId)
            if (null != existingCartItemProperties) {
                Log.i(tag, "---\tCartItemProperties: $cartItemProperties")
                cartDao.remove(existingCartItemProperties)
            } else {
                Log.i(tag, "Could not find cartItemProperties to remove. Doing nothing")
            }
        }
    }

    private suspend fun add(newCartItemProperties: CartItemProperties) {
        val existingCartItemProperties =
            cartDao.getCartItemByItemId(newCartItemProperties.itemId, newCartItemProperties.inCart)
        if (null == existingCartItemProperties) {
            println("+++\tCartItemProperties\t $newCartItemProperties")
            cartDao.save(newCartItemProperties)
        } else {
            println("vvv\tCartItemProperties\t $newCartItemProperties")
            cartDao.updateAll(newCartItemProperties)
        }
    }
}
