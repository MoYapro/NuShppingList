package de.moyapro.nushppinglist

import kotlinx.coroutines.flow.Flow

interface CartDao {

    fun save(vararg cartItemProperties: CartItemProperties)
    fun findAll(): Flow<List<CartItem>>

}
