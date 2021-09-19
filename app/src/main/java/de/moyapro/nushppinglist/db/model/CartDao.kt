package de.moyapro.nushppinglist.db.model

import androidx.room.*
import de.moyapro.nushppinglist.db.ids.ID
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

    @Transaction
    @Query("select * from CartItemProperties")
    fun findAllCartItems(): Flow<List<CartItem>>

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

    @Insert
    fun save(test: Test)

    @Deprecated(
        "This is just for the generated Dao_Impl",
        level = DeprecationLevel.WARNING,
        replaceWith = ReplaceWith("getByIdRealId(theID)")
    )
    @Query("select * from test where id = :theID")
    fun getByIdLongType(theID: Long): Test

}

fun CartDao.getByIdRealId(theID: ID): Test = getByIdRealId(theID.id)
