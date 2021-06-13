package de.moyapro.nushppinglist

import kotlinx.coroutines.flow.Flow

interface CartDao {

    fun save(vararg cartItemProperties: CartItemProperties)
    fun save(vararg items: Item)
    fun save(vararg cartItems: CartItem)
    fun findAll(): Flow<List<CartItem>>
    fun findNotAddedItems(): List<Item>

}
