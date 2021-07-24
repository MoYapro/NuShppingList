package de.moyapro.nushppinglist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
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

}
