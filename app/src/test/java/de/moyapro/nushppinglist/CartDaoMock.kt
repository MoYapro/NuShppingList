package de.moyapro.nushppinglist

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class CartDaoMock(
    private val externalScope: CoroutineScope
) : CartDao {

    private val itemTable: MutableSet<Item> = mutableSetOf()
    private val cartItemPropertiesTable: MutableSet<CartItemProperties> = mutableSetOf()
    private val relationTable: MutableSet<CartItem> = mutableSetOf()

    private val channel = ConflatedBroadcastChannel<List<CartItem>>()

    val cartItemFlow: Flow<List<CartItem>> = channel.asFlow().shareIn(
        externalScope,
        replay = 1,
        started = SharingStarted.WhileSubscribed()
    )


    override fun save(vararg cartItemProperties: CartItemProperties) {
        cartItemPropertiesTable += cartItemProperties
    }

    override fun save(vararg items: Item) {
        itemTable += items
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
            channel.send(relationTable.toList())
        }
    }

    override fun findAll(): Flow<List<CartItem>> {
        return cartItemFlow
    }

    override fun findNotAddedItems(): List<Item> {
        return itemTable.toList()
    }

}
