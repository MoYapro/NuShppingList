package de.moyapro.nushppinglist.sync.handler

import android.util.Log
import de.moyapro.nushppinglist.constants.SWITCHES
import de.moyapro.nushppinglist.db.dao.*
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.db.model.CartItemProperties
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.messages.CartMessage
import de.moyapro.nushppinglist.sync.messages.RequestCartListMessage
import de.moyapro.nushppinglist.sync.messages.RequestItemMessage
import de.moyapro.nushppinglist.util.takeIfNotDefault
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CartMessageHandler(
    val cartDao: CartDao,
    val publisher: Publisher?,
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
            Log.w(
                tag,
                "Could not handle cart message $cartMessage after ${SWITCHES.WAIT_FOR_MISSING_ITEMS_TIMEOUT}ms"
            )
            return
        }

        if(requestMissingCarts(cartMessage)) {
            Log.i(tag, "Did not process cartMessage, cart does not exist and was therefor requested")
            return
        }
        val requestedItems = requestMissingItemIds(cartMessage)
        removeZeroAmountItemsFromDb(cartMessage)
        if (requestedItems.isNotEmpty()) {
            Log.i(tag, "Did not find items for #$requestedItems itemIds")
        }

        persistCartItemProperties(
            cartMessage.cartItemPropertiesList.filter { it.itemId !in requestedItems }
        )
    }

    private suspend fun requestMissingCarts(cartMessage: CartMessage): Boolean {
        val existingCart = cartMessage.cartId?.let { cartDao.getCartByCartId(it) }
        return if (null == existingCart) {
            Log.i(tag, "request non existing cart with cartId ${cartMessage.cartId}")
            publisher?.publish(RequestCartListMessage("Missing: ${listOf(cartMessage.cartId)}"))
            true
        } else {
            false
        }
    }

    private suspend fun persistCartItemProperties(cartItemPropertiesList: List<CartItemProperties>) {
        cartItemPropertiesList.forEach { add(it) }
    }

    private fun removeZeroAmountItemsFromDb(cartMessage: CartMessage) {
        cartMessage.cartItemPropertiesList.filter { 0 >= it.amount }.forEach(::remove)
    }

    private suspend fun requestMissingItemIds(cartMessage: CartMessage): List<ItemId> {
        val requestedItemIds = cartMessage.cartItemPropertiesList.map(CartItemProperties::itemId)
        val availableItemIds = cartDao.getAllItemByItemId(requestedItemIds).map(Item::itemId)
        val missingItemIds = requestedItemIds - availableItemIds

        if (missingItemIds.isNotEmpty()) {
            Log.i(tag, "request non existing item with itemId $missingItemIds")
            publisher?.publish(RequestItemMessage(missingItemIds))
        }
        return missingItemIds
    }

    private fun remove(cartItemProperties: CartItemProperties) {
        println("---\tCartItemProperties: $cartItemProperties")
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            val existingCartItemProperties = cartDao.getCartItemByItemId(
                cartItemProperties.itemId,
                cartDao.getSelectedCart()?.cartId
            )
            if (null != existingCartItemProperties) {
                Log.i(tag, "---\tCartItemProperties: $cartItemProperties")
                cartDao.remove(existingCartItemProperties)
            } else {
                Log.i(tag, "Could not find cartItemProperties to remove. Doing nothing")
            }
        }
    }

    private suspend fun add(newCartItemProperties: CartItemProperties) {
        val cartItemInDb =
            cartDao.getCartItemByCartItemPropertiesId(newCartItemProperties.cartItemPropertiesId)
        val cartItemWithSameProperties =
            cartDao.getCartItemByItemId(newCartItemProperties.itemId, newCartItemProperties.inCart)
        val cartItemWithSameId =
            cartDao.getCartItemByCartItemPropertiesId(newCartItemProperties.cartItemPropertiesId)
        when {
            cartItemInDb == newCartItemProperties -> return
            null == cartItemInDb && null == cartItemWithSameProperties -> cartDao.save(
                newCartItemProperties
            )
            null == cartItemInDb && null != cartItemWithSameProperties -> cartDao.updateAll(
                merge(cartItemWithSameProperties, newCartItemProperties)
            )
            null != cartItemInDb && null == cartItemWithSameProperties -> cartDao.updateAll(
                merge(cartItemInDb, newCartItemProperties)
            )
            null != cartItemInDb && null != cartItemWithSameProperties -> {
                cartDao.remove(cartItemInDb)
                cartDao.updateAll(
                    merge(
                        merge(cartItemInDb, cartItemWithSameProperties),
                        newCartItemProperties
                    )
                )
            }
        }
    }

    fun merge(
        originalCartItemProperties: CartItemProperties,
        updatedCartItemProperties: CartItemProperties,
    ): CartItemProperties {
        val default = CartItemProperties()
        return CartItemProperties(
            cartItemPropertiesId = originalCartItemProperties.cartItemPropertiesId,
            cartItemId = takeIfNotDefault(
                originalCartItemProperties,
                default,
                updatedCartItemProperties,
                CartItemProperties::cartItemId
            ),
            inCart = takeIfNotDefault(
                originalCartItemProperties,
                default,
                updatedCartItemProperties,
                CartItemProperties::inCart
            ),
            itemId = takeIfNotDefault(
                originalCartItemProperties,
                default,
                updatedCartItemProperties,
                CartItemProperties::itemId
            ),
            recipeId = takeIfNotDefault(
                originalCartItemProperties,
                default,
                updatedCartItemProperties,
                CartItemProperties::recipeId
            ),
            amount = takeIfNotDefault(
                originalCartItemProperties,
                default,
                updatedCartItemProperties,
                CartItemProperties::amount
            ),
            checked = takeIfNotDefault(
                originalCartItemProperties,
                default,
                updatedCartItemProperties,
                CartItemProperties::checked
            ),
        )
    }
}
