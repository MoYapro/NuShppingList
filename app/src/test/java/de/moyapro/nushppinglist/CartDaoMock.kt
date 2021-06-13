package de.moyapro.nushppinglist

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn

class CartDaoMock(
    private val externalScope: CoroutineScope
) : CartDao {

    private val itemTable: MutableSet<Item> = mutableSetOf()
    private val cartItemPropertiesTable: MutableSet<CartItemProperties> = mutableSetOf()
    private val relationTable: MutableSet<CartItem> = mutableSetOf()

    val cartItemFlow: Flow<List<CartItem>> = flow<List<CartItem>> {
        emptySet<CartItem>()
    }.shareIn(
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
            relationTable += cartItem
        }
    }

    override fun findAll(): Flow<List<CartItem>> {
        return cartItemFlow
    }

    override fun findNotAddedItems(): List<Item> {
     return itemTable.toList()
    }

}
