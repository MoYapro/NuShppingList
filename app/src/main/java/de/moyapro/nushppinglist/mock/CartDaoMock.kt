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
    private val relationTable: MutableSet<CartItem> = mutableSetOf()

    private val cartItemChannel = ConflatedBroadcastChannel<List<CartItemProperties>>()
    private val allItemChannel = ConflatedBroadcastChannel<List<Item>>()

    private val allItemFlow: Flow<List<Item>> = allItemChannel.asFlow().shareIn(
        externalScope,
        replay = 1,
        started = SharingStarted.WhileSubscribed()
    )

    private val cartItemFlow: Flow<List<CartItemProperties>> = cartItemChannel.asFlow().shareIn(
        externalScope,
        replay = 1,
        started = SharingStarted.WhileSubscribed()
    )


    override fun save(vararg cartItemProperties: CartItemProperties) {
        cartItemPropertiesTable += cartItemProperties
        externalScope.launch {
            cartItemChannel.send(cartItemPropertiesTable.toList())
        }
    }

    override fun save(vararg items: Item) {
        itemTable += items
        externalScope.launch {
            allItemChannel.send(itemTable.toList())
        }
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
        externalScope.launch {
            allItemChannel.send(itemTable.toList())
        }
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
        externalScope.launch {
            cartItemChannel.send(cartItemPropertiesTable.toList())
        }
    }

    private fun save(cartItem: CartItem) {
        relationTable += cartItem
        externalScope.launch {
            cartItemChannel.send(relationTable.map { it.cartItemProperties })
        }
    }

    override fun findAllInCart(): Flow<List<CartItemProperties>> {
        return cartItemFlow
    }

    override fun findAllItems(): Flow<List<Item>> {
        return allItemFlow
    }

    override fun findNotAddedItems(): List<Item> {
        return itemTable.toList()
    }

    override fun getItemByItemId(itemId: Long): Item? {
        return itemTable.firstOrNull { itemInDb -> itemInDb.itemId == itemId }
    }

    override fun getItemByItemName(itemName: String): Item? {
        return this.itemTable.firstOrNull { it.name == itemName }
    }

    fun reset() {
        itemTable.clear()
        cartItemPropertiesTable.clear()
        relationTable.clear()
        externalScope.launch {
            cartItemChannel.send(cartItemPropertiesTable.toList())
            allItemChannel.send(itemTable.toList())
        }
    }

}
