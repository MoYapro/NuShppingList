package de.moyapro.nushppinglist.db.dao

import androidx.room.*
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.CartItemProperties
import de.moyapro.nushppinglist.db.model.Item
import kotlinx.coroutines.flow.Flow

@Suppress("FunctionName")
@Dao
interface CartDao {

    @Transaction
    @Insert
    fun save(vararg cartItemProperties: CartItemProperties)

    @Transaction
    @Insert
    fun save(vararg items: Item)

    @Transaction
    @Update
    fun updateAll(vararg items: Item)

    @Transaction
    @Update
    fun updateAll(vararg items: CartItemProperties)

    @Transaction
    @Query("select * from CartItemProperties")
    fun findAllInCart(): Flow<List<CartItemProperties>>

    @Transaction
    @Query("select * from Item")
    fun findAllItems(): Flow<List<Item>>

    @Transaction
    @Query("select * from CartItemProperties join Item on Item.itemId = CartItemProperties.itemId")
    fun findAllCartItems(): Flow<List<CartItem>>

    @Transaction
    @Query("select * from Item")
    fun findNotAddedItems(): List<Item>

    @Deprecated(
        "This is just for the generated Dao_Impl",
        level = DeprecationLevel.WARNING,
        replaceWith = ReplaceWith("getItemByItemId(itemId)")
    )
    @Transaction
    @Query("select * from Item i where i.itemId = :itemId")
    fun getItemByItemId_internal(itemId: Long): Item?

    @Deprecated(
        "This is just for the generated Dao_Impl",
        level = DeprecationLevel.WARNING,
        replaceWith = ReplaceWith("getCartItemByItemId(itemId)")
    )
    @Transaction
    @Query("select * from CartItemProperties p where p.itemId = :itemId")
    fun getCartItemByItemId_internal(itemId: Long): CartItemProperties?

    @Transaction
    @Query("select * from Item i where i.name = :itemName")
    fun getItemByItemName(itemName: String): Item?

    @Transaction
    @Delete
    fun remove(cartItem: CartItemProperties)

}

fun CartDao.getCartItemByItemId(itemId: ItemId) = getCartItemByItemId_internal(itemId.id)
fun CartDao.getItemByItemId(itemId: ItemId): Item? = getItemByItemId_internal(itemId.id)

