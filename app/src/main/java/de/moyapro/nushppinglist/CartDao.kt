package de.moyapro.nushppinglist

import kotlinx.coroutines.flow.Flow

interface CartDao {

    fun save(vararg cartItemProperties: CartItemProperties)
    fun save(vararg items: Item)
    fun save(vararg cartItems: CartItem)
    fun findAllInCart(): Flow<List<CartItem>>
    fun findAllItems(): Flow<List<Item>>
    fun findNotAddedItems(): List<Item>

}
