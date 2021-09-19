package de.moyapro.nushppinglist.db.model

import androidx.room.*
import de.moyapro.nushppinglist.db.ids.ID
import de.moyapro.nushppinglist.db.ids.ItemId
import kotlinx.coroutines.flow.Flow

@Suppress("FunctionName")
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

    @Deprecated(
        "This is just for the generated Dao_Impl",
        level = DeprecationLevel.WARNING,
        replaceWith = ReplaceWith("getItemByItemId(itemId)")
    )
    @Query("select * from Item i where i.itemId = :itemId")
    fun getItemByItemId_internal(itemId: Long): Item?

    @Deprecated(
        "This is just for the generated Dao_Impl",
        level = DeprecationLevel.WARNING,
        replaceWith = ReplaceWith("getCartItemByItemId(itemId)")
    )
    @Query("select * from CartItemProperties p where p.itemId = :itemId")
    fun getCartItemByItemId_internal(itemId: Long): CartItemProperties?

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

fun CartDao.getByIdRealId(theID: ID) = getByIdRealId(theID.id)
fun CartDao.getCartItemByItemId(itemId: ItemId) = getCartItemByItemId(itemId.id)
fun CartDao.getItemByItemId(itemId: ItemId): Item? = getItemByItemId(itemId.id)

