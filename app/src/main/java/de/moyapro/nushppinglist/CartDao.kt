package de.moyapro.nushppinglist

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Insert
    fun save(vararg cartItemProperties: CartItemProperties)

    @Insert
    fun save(vararg items: Item)

    @Update
    fun updateAll(vararg items: Item)

    @Update
    fun updateAll(vararg items: CartItemProperties)

    @Query("select * from CartItemProperties")
    fun findAllInCart(): Flow<List<CartItemProperties>>

    @Query("select * from Item")
    fun findAllItems(): Flow<List<Item>>

    @Query("select * from Item")
    fun findNotAddedItems(): List<Item>

    @Query("select * from Item i where i.itemId = :itemId")
    fun getItemByItemId(itemId: Long): Item?

    @Query("select * from CartItemProperties p where p.itemId = :itemId")
    fun getCartItemByItemId(itemId: Long): CartItemProperties?

    @Query("select * from Item i where i.name = :itemName")
    fun getItemByItemName(itemName: String): Item?

    @Delete
    fun remove(cartItem: CartItemProperties)

}
