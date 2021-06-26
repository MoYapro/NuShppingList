package de.moyapro.nushppinglist

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

    private val cartItemChannel = ConflatedBroadcastChannel<List<CartItem>>()
    private val allItemChannel = ConflatedBroadcastChannel<List<Item>>()

    private val allItemFlow: Flow<List<Item>> = allItemChannel.asFlow().shareIn(
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
    }

    override fun save(vararg items: Item) {
        itemTable += items
        externalScope.launch {
            allItemChannel.send(itemTable.toList())
        }
    }

    override fun save(vararg cartItems: CartItem) {
        cartItems.forEach { cartItem ->
            save(cartItem.item)
            save(cartItem.cartItemProperties)
            save(cartItem)
        }
    }

    private fun save(cartItem: CartItem) {
        relationTable += cartItem
        externalScope.launch {
            cartItemChannel.send(relationTable.toList())
        }
    }

    override fun findAllInCart(): Flow<List<CartItem>> {
        return cartItemFlow
    }

    override fun findAllItems(): Flow<List<Item>> {
        return allItemFlow
    }

    override fun findNotAddedItems(): List<Item> {
        return itemTable.toList()
    }

}
