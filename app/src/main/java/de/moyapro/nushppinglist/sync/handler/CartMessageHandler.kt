package de.moyapro.nushppinglist.sync.handler

import android.util.Log
import de.moyapro.nushppinglist.db.dao.*
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.db.model.CartItemProperties
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.messages.CartMessage
import de.moyapro.nushppinglist.sync.messages.ItemMessage
import de.moyapro.nushppinglist.sync.messages.RequestCartListMessage
import de.moyapro.nushppinglist.sync.messages.RequestItemMessage
import de.moyapro.nushppinglist.util.takeIfNotDefault
import kotlinx.coroutines.*

class CartMessageHandler(
    val cartDao: CartDao,
    val publisher: Publisher? = null,
) : suspend (CartMessage) -> Unit {

    private val tag = CartMessageHandler::class.simpleName
    private val itemMessageHandler = ItemMessageHandler(cartDao, publisher)

    override suspend fun invoke(cartMessage: CartMessage) {
        Log.d(tag, "start handle $cartMessage")

        saveItems(cartMessage.itemList)


        if (requestMissingCarts(cartMessage)) {
            Log.d(
                tag,
                "Did not process cartMessage, cart does not exist and was therefor requested"
            )
            return
        }
        removeZeroAmountItemsFromDb(cartMessage)
        val requestedItems = requestMissingItemIds(cartMessage)
        if (requestedItems.isNotEmpty()) {
            Log.d(tag, "Did not find items for #$requestedItems itemIds")
        }

        persistCartItemProperties(cartMessage.cartItemPropertiesList)
        Log.d(tag, "done handle $cartMessage")
    }

    private fun saveItems(itemList: List<Item>) = runBlocking {
        itemMessageHandler(ItemMessage(itemList))
    }

    private suspend fun requestMissingCarts(cartMessage: CartMessage): Boolean {
        val existingCart = cartMessage.cartId?.let { cartDao.getCartByCartId(it) }
        if (null == cartMessage.cartId) return false
        return if (null == existingCart) {
            Log.d(tag, "request non existing cart with cartId ${cartMessage.cartId}")
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
            Log.d(tag, "request non existing item with itemId $missingItemIds")
            publisher?.publish(RequestItemMessage(missingItemIds))
        }
        return missingItemIds
    }

    private fun remove(cartItemProperties: CartItemProperties) {
        Log.d(tag, "---\tCartItemProperties: $cartItemProperties")
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            val existingCartItemProperties = cartDao.getCartItemByItemId(
                cartItemProperties.itemId,
                cartDao.getSelectedCart()?.cartId
            )
            if (null != existingCartItemProperties) {
                Log.d(tag, "---\tCartItemProperties: $cartItemProperties")
                cartDao.remove(existingCartItemProperties)
            } else {
                Log.d(tag, "Could not find cartItemProperties to remove. Doing nothing")
            }
        }
    }

    private suspend fun add(newCartItemProperties: CartItemProperties) {
        val cartItemInDb =
            cartDao.getCartItemByCartItemPropertiesId(newCartItemProperties.cartItemPropertiesId)
        val cartItemWithSameItemId =
            cartDao.getCartItemByItemId(newCartItemProperties.itemId, newCartItemProperties.inCart)
        when {
            cartItemInDb == newCartItemProperties -> {
                Log.d(tag, "Item already exists $newCartItemProperties")
                return
            }
            null == cartItemInDb && null == cartItemWithSameItemId -> {
                Log.d(tag, "Item does not exist $newCartItemProperties")
                cartDao.save(newCartItemProperties)
            }
            null == cartItemInDb && null != cartItemWithSameItemId -> {
                Log.d(
                    tag,
                    "Update existing itemProperties for same item $cartItemWithSameItemId with new values: $newCartItemProperties "
                )
                mergeAndUpdate(cartItemWithSameItemId, newCartItemProperties)
            }
            null != cartItemInDb && null == cartItemWithSameItemId -> {
                Log.d(
                    tag,
                    "Update existing itemProperties $cartItemWithSameItemId with new values: $newCartItemProperties "
                )
                mergeAndUpdate(cartItemInDb, newCartItemProperties)
            }
            null != cartItemInDb && null != cartItemWithSameItemId -> {
                Log.d(tag, "Strange state just merge it")
                mergeAndUpdate(cartItemInDb, cartItemWithSameItemId, newCartItemProperties)
            }
        }
    }

    private suspend fun mergeAndUpdate(vararg cartItemProperties: CartItemProperties) {
        val mergedCartItemProperties = cartItemProperties.reduce(this::merge)
        Log.d(tag, "Merge result: $mergedCartItemProperties")
        cartDao.updateAll(mergedCartItemProperties)
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
            checked = updatedCartItemProperties.checked,
        )
    }
}
