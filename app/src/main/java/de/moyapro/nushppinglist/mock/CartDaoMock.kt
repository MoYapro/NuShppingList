package de.moyapro.nushppinglist.mock

import android.util.Log
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.dao.getItemByItemId
import de.moyapro.nushppinglist.db.model.Cart
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.CartItemProperties
import de.moyapro.nushppinglist.db.model.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@FlowPreview
class CartDaoMock(
    private val externalScope: CoroutineScope,
) : CartDao {

    val tag = CartDaoMock::class.simpleName

    val itemTable: MutableSet<Item> = ConcurrentHashMap.newKeySet()
    val cartItemPropertiesTable: MutableSet<CartItemProperties> =
        ConcurrentHashMap.newKeySet()
    val cartTable: MutableSet<Cart> = ConcurrentHashMap.newKeySet()

    private val cartChannel: MutableStateFlow<List<Cart>> = MutableStateFlow(listOf())
    private val cartItemChannel: MutableStateFlow<List<CartItem>> = MutableStateFlow(listOf())
    private val selectedCartItemChannel: MutableStateFlow<List<CartItem>> =
        MutableStateFlow(listOf())
    private val cartItemPropertiesChannel: MutableStateFlow<List<CartItemProperties>> =
        MutableStateFlow(listOf())
    private val allItemChannel: MutableStateFlow<List<Item>> = MutableStateFlow(listOf())
    private val allItemFlow: Flow<List<Item>> = allItemChannel
    private val cartItemPropertiesFlow: Flow<List<CartItemProperties>> = cartItemPropertiesChannel
    private val cartItemFlow: Flow<List<CartItem>> = cartItemChannel
    private val selectedCartItemFlow: Flow<List<CartItem>> = selectedCartItemChannel
    private val cartFlow: Flow<List<Cart>> = cartChannel

    override suspend fun save(vararg cartItemProperties: CartItemProperties) {
        cartItemProperties.forEach {
            Log.d(tag, "vvv\t Add $it")
        }
        cartItemPropertiesTable += cartItemProperties
        pushCartItemProperties()
        pushCartItems()
    }

    override suspend fun save(vararg items: Item) {
        itemTable += items
        pushItems()
        pushCartItems()
    }

    override suspend fun save(vararg carts: Cart) {
        cartTable.addAll(carts)
        pushCart()
    }

    override suspend fun updateAll(vararg items: Item) {
        val toUpdate = items.associateBy({ it.itemId }, { it })
        Log.d(tag, "vvv\t Update $items")
        val updatedItemTable: List<Item> = itemTable.map { itemFromTable ->
            if (toUpdate.containsKey(itemFromTable.itemId)) {
                toUpdate[itemFromTable.itemId]!!
            } else {
                itemFromTable
            }
        }
        itemTable.clear()
        itemTable.addAll(updatedItemTable.toSet())
        pushItems()
    }

    override suspend fun updateAll(vararg items: CartItemProperties) {
        val toUpdate = items.associateBy({ it.itemId }, { it })
        Log.d(tag, "vvv\t Update $toUpdate")
        val updatedCartItemPropertiesTable: List<CartItemProperties> =
            cartItemPropertiesTable.map { itemFromTable ->
                if (toUpdate.containsKey(itemFromTable.itemId)) {
                    toUpdate[itemFromTable.itemId]!!
                } else {
                    itemFromTable
                }
            }
        cartItemPropertiesTable.clear()
        cartItemPropertiesTable.addAll(updatedCartItemPropertiesTable.toSet())
        pushCartItemProperties()
    }

    override suspend fun updateAll(vararg carts: Cart) {
        val toUpdate = carts.associateBy({ it.cartId }, { it })
        val updatedCartTable: List<Cart> =
            cartTable.map { cartFromTable ->
                if (toUpdate.containsKey(cartFromTable.cartId)) {
                    toUpdate[cartFromTable.cartId]!!
                } else {
                    cartFromTable
                }
            }
        cartTable.clear()
        cartTable.addAll(updatedCartTable.toSet())
        pushCart()
    }

    override fun findAllInCart(): Flow<List<CartItemProperties>> {
        return cartItemPropertiesFlow
    }

    override fun findAllItems(): Flow<List<Item>> {
        return allItemFlow
    }

    override fun findAllCartItems(): Flow<List<CartItem>> {
        return cartItemFlow
    }

    override fun findAllSelectedCartItems_internal(cartId: UUID?): Flow<List<CartItem>> {
        Log.d(tag, "find all selected cart Items")
        return MutableStateFlow(cartItemPropertiesTable
            .filter {

                val isInList = it.inCart?.id == cartId
                isInList
            }
            .map { cartItemProperties ->
                val item = itemTable.first { item -> item.itemId == cartItemProperties.itemId }
                Log.d(tag, "creating cartItem from $cartItemProperties and $item")
                CartItem(
                    cartItemProperties,
                    item
                )
            }
        )
    }

    override fun findAllCart(): Flow<List<Cart>> {
        return cartFlow
    }

    override fun findSelectedCart(): Flow<Cart?> {
        TODO("Not yet implemented")
    }

    override suspend fun getSyncedCarts(): List<Cart> {
        return cartTable.filter(Cart::synced)
    }

    override suspend fun getAllCartItems(): List<CartItem> {
        return cartItemPropertiesTable.map {
            CartItem(
                cartItemProperties = it,
                item = getItemByItemId(it.itemId)!!
            )
        }
    }

    override fun findNotAddedItems(): List<Item> {
        return itemTable.toList()
    }

    override suspend fun getItemByItemId_internal(itemId: UUID): Item? {
        return itemTable.firstOrNull { itemInDb -> itemInDb.itemId.id == itemId }
    }

    override suspend fun getAllItemByItemId_internal(itemId: List<UUID>): List<Item> {
        return itemId.mapNotNull { getItemByItemId_internal(it) }
    }

    override suspend fun getCartItemByItemId_internal(
        itemId: UUID,
        cartId: UUID?,
    ): CartItemProperties? {
        return cartItemPropertiesTable.singleOrNull { itemId == it.itemId.id && it.inCart?.id == cartId }
    }

    override suspend fun getCartItemByCartItemId_internal(cartItemId: UUID): CartItemProperties? {
        return cartItemPropertiesTable.singleOrNull { it.cartItemPropertiesId == cartItemId }
    }

    override suspend fun getCartItemByCartItemPropertiesId_internal(cartItemPropertiesId: UUID): CartItemProperties? {
        return cartItemPropertiesTable.singleOrNull { it.cartItemPropertiesId == cartItemPropertiesId }
    }

    override suspend fun getItemByItemName(itemName: String): Item? {
        return this.itemTable.firstOrNull { it.name == itemName }
    }

    override suspend fun getSelectedCart(): Cart? = this.cartTable.firstOrNull { it.selected }

    override suspend fun remove(cartItem: CartItemProperties) {
        Log.d(tag, "---\t Remove $cartItem")
        cartItemPropertiesTable.removeIf { it.cartItemId == cartItem.cartItemId }
        pushCartItemProperties()

    }

    override suspend fun remove(item: Item) {
        itemTable.remove(item)
        pushItems()
    }

    override suspend fun remove(cart: Cart) {
        cartTable.remove(cart)
        pushCart()
    }

    override suspend fun selectCart(cartId: UUID?) {
        TODO("Not yet implemented")
    }

    override suspend fun getCartByCartId_internal(cartId: UUID): Cart? {
        return cartTable.singleOrNull { it.cartId.id == cartId }
    }

    private fun pushCartItems() {
        val cartItemJoinTable = getJoin(itemTable, cartItemPropertiesTable)
        externalScope.launch {
            val selectedCartId = getSelectedCart()?.cartId
            cartItemChannel.value = cartItemJoinTable
            selectedCartItemChannel.value =
                cartItemJoinTable.filter { null == selectedCartId || it.cartItemProperties.inCart == selectedCartId }
        }
    }

    private fun pushCart() {
        externalScope.launch {
            cartChannel.value = cartTable.toList()
        }
        pushCartItems()
    }

    private fun getJoin(
        itemTable: Set<Item>,
        cartItemPropertiesTable: Set<CartItemProperties>,
    ): List<CartItem> {
        return cartItemPropertiesTable
            .mapNotNull { cartItemProperties ->
                val joinedItem =
                    itemTable.firstOrNull { item -> item.itemId == cartItemProperties.itemId }
                when {
                    null != joinedItem -> CartItem(cartItemProperties, joinedItem)
                    else -> null
                }
            }
    }


    private fun pushItems() {
        externalScope.launch {
            allItemChannel.value = itemTable.toList()
        }
    }

    private fun pushCartItemProperties() {
        externalScope.launch {
            cartItemPropertiesChannel.value = cartItemPropertiesTable.toList()
        }
    }

    fun reset() {
        itemTable.clear()
        cartItemPropertiesTable.clear()
        cartTable.clear()
        externalScope.launch {
            cartItemPropertiesChannel.value = cartItemPropertiesTable.toList()
            allItemChannel.value = itemTable.toList()
        }
    }

}
