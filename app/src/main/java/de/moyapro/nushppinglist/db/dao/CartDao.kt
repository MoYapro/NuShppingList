package de.moyapro.nushppinglist.db.dao

import androidx.room.*
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.db.model.Cart
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.CartItemProperties
import de.moyapro.nushppinglist.db.model.Item
import kotlinx.coroutines.flow.Flow
import java.util.*

@Suppress("FunctionName")
@Dao
interface CartDao {

    @Transaction
    @Insert
    suspend fun save(vararg cartItemProperties: CartItemProperties)

    @Transaction
    @Insert
    suspend fun save(vararg items: Item)

    @Transaction
    @Insert
    suspend fun save(vararg cart: Cart)

    @Transaction
    @Update
    suspend fun updateAll(vararg items: Item)

    @Transaction
    @Update
    suspend fun updateAll(vararg items: CartItemProperties)

    @Transaction
    @Update
    suspend fun updateAll(vararg items: Cart)

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
    @Query("select * from Cart")
    fun findAllCart(): Flow<List<Cart>>

    @Transaction
    @Query("select * from CartItemProperties join Item on Item.itemId = CartItemProperties.itemId")
    suspend fun getAllCartItems(): List<CartItem>

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
    suspend fun getItemByItemId_internal(itemId: UUID): Item?

    @Deprecated(
        "This is just for the generated Dao_Impl",
        level = DeprecationLevel.WARNING,
        replaceWith = ReplaceWith("getAllItemByItemId(itemId)")
    )
    @Transaction
    @Query("select * from Item i where i.itemId in (:itemId)")
    suspend fun getAllItemByItemId_internal(itemId: List<UUID>): List<Item>

    @Deprecated(
        "This is just for the generated Dao_Impl",
        level = DeprecationLevel.WARNING,
        replaceWith = ReplaceWith("getCartItemByItemId(itemId)")
    )
    @Transaction
    @Query("select * from CartItemProperties p where p.itemId = :itemId")
    suspend fun getCartItemByItemId_internal(itemId: UUID): CartItemProperties?

    @Transaction
    @Query("select * from Item i where i.name = :itemName")
    suspend fun getItemByItemName(itemName: String): Item?

    @Transaction
    @Delete
    suspend fun remove(cartItem: CartItemProperties)

    @Transaction
    @Delete
    suspend fun remove(item: Item)

    @Transaction
    @Delete
    suspend fun remove(item: Cart)

}

suspend fun CartDao.getCartItemByItemId(itemId: ItemId) = getCartItemByItemId_internal(itemId.id)
suspend fun CartDao.getItemByItemId(itemId: ItemId): Item? = getItemByItemId_internal(itemId.id)
suspend fun CartDao.getAllItemByItemId(itemIds: List<ItemId>): List<Item> = getAllItemByItemId_internal(itemIds.map{it.id})

