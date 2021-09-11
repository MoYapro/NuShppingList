package de.moyapro.nushppinglist.mock

import de.moyapro.nushppinglist.CartDao
import de.moyapro.nushppinglist.CartItem
import de.moyapro.nushppinglist.CartItemProperties
import de.moyapro.nushppinglist.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
class CartDaoMock(
    private val externalScope: CoroutineScope
) : CartDao {

    private val itemTable: MutableSet<Item> = mutableSetOf()
    private val cartItemPropertiesTable: MutableSet<CartItemProperties> = mutableSetOf()

    private val cartItemChannel = ConflatedBroadcastChannel<List<CartItem>>()
    private val cartItemPropertiesChannel = ConflatedBroadcastChannel<List<CartItemProperties>>()
    private val allItemChannel = ConflatedBroadcastChannel<List<Item>>()

    private val allItemFlow: Flow<List<Item>> = allItemChannel.asFlow().shareIn(
        externalScope,
        replay = 1,
        started = SharingStarted.WhileSubscribed()
    )

    private val cartItemPropertiesFlow: Flow<List<CartItemProperties>> =
        cartItemPropertiesChannel.asFlow().shareIn(
            externalScope,
            replay = 1,
            started = SharingStarted.WhileSubscribed()
        )


    private val cartItemFlow: Flow<List<CartItem>> = cartItemChannel.asFlow().shareIn(
        externalScope,
        replay = 1,
        started = SharingStarted.WhileSubscribed()
    )


    override fun save(vararg cartItemProperties: CartItemProperties) {
        cartItemPropertiesTable += cartItemProperties
        pushCartItemProperties()
        pushCartItems()
    }

    override fun save(vararg items: Item) {
        itemTable += items
        pushItems()
        pushCartItems()
    }

    override fun updateAll(vararg items: Item) {
        val toUpdate = items.associateBy({ it.itemId }, { it })
        val updatedItemTable: List<Item> = itemTable.map { itemFromTable ->
            if (toUpdate.containsKey(itemFromTable.itemId)) {
                toUpdate[itemFromTable.itemId]!!
            } else {
                itemFromTable
            }
        }
        itemTable.clear()
        itemTable.addAll(updatedItemTable.toSet())
        pushItems()
    }

    override fun updateAll(vararg items: CartItemProperties) {
        val toUpdate = items.associateBy({ it.itemId }, { it })
        val updatedItemTable: List<CartItemProperties> =
            cartItemPropertiesTable.map { itemFromTable ->
                if (toUpdate.containsKey(itemFromTable.itemId)) {
                    toUpdate[itemFromTable.itemId]!!
                } else {
                    itemFromTable
                }
            }
        cartItemPropertiesTable.clear()
        cartItemPropertiesTable.addAll(updatedItemTable.toSet())
        pushCartItemProperties()
    }

    override fun findAllInCart(): Flow<List<CartItemProperties>> {
        return cartItemPropertiesFlow
    }

    override fun findAllItems(): Flow<List<Item>> {
        return allItemFlow
    }

    override fun findAllCartItems(): Flow<List<CartItem>> {
        return cartItemFlow
    }

    override fun findNotAddedItems(): List<Item> {
        return itemTable.toList()
    }

    override fun getItemByItemId(itemId: Long): Item? {
        return itemTable.firstOrNull { itemInDb -> itemInDb.itemId == itemId }
    }

    override fun getCartItemByItemId(itemId: Long): CartItemProperties? {
        return cartItemPropertiesTable.firstOrNull { itemId == it.itemId }
    }

    override fun getItemByItemName(itemName: String): Item? {
        return this.itemTable.firstOrNull { it.name == itemName }
    }

    override fun remove(cartItem: CartItemProperties) {
        cartItemPropertiesTable.removeIf { it.checked }
        pushCartItemProperties()

    }

    private fun pushCartItems() {
        val cartItemJoinTable = getJoin(itemTable, cartItemPropertiesTable)
        externalScope.launch {
            cartItemChannel.send(cartItemJoinTable)
        }
    }

    private fun getJoin(
        itemTable: Set<Item>,
        cartItemPropertiesTable: Set<CartItemProperties>
    ): List<CartItem> {
        return cartItemPropertiesTable
            .mapNotNull { cartItemProperties ->
                val joinedItem =
                    itemTable.firstOrNull { item -> item.itemId == cartItemProperties.itemId }
                when {
                    null != joinedItem -> CartItem(cartItemProperties, joinedItem)
                    else -> null
                }
            }
    }


    private fun pushItems() {
        externalScope.launch {
            allItemChannel.send(itemTable.toList())
        }
    }

    private fun pushCartItemProperties() {
        externalScope.launch {
            cartItemPropertiesChannel.send(cartItemPropertiesTable.toList())
        }
    }

    fun reset() {
        itemTable.clear()
        cartItemPropertiesTable.clear()
        externalScope.launch {
            cartItemPropertiesChannel.send(cartItemPropertiesTable.toList())
            allItemChannel.send(itemTable.toList())
        }
    }

}
