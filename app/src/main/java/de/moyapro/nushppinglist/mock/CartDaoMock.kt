package de.moyapro.nushppinglist.mock

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

    private val itemTable: MutableSet<Item> = ConcurrentHashMap.newKeySet()
    private val cartItemPropertiesTable: MutableSet<CartItemProperties> =
        ConcurrentHashMap.newKeySet()
    private val cartTable: MutableSet<Cart> = ConcurrentHashMap.newKeySet()

    private val cartChannel: MutableStateFlow<List<Cart>> = MutableStateFlow(listOf())
    private val cartItemChannel: MutableStateFlow<List<CartItem>> = MutableStateFlow(listOf())
    private val selectedCartItemChannel: MutableStateFlow<List<CartItem>> = MutableStateFlow(listOf())
    private val cartItemPropertiesChannel: MutableStateFlow<List<CartItemProperties>> =
        MutableStateFlow(listOf())
    private val allItemChannel: MutableStateFlow<List<Item>> = MutableStateFlow(listOf())
    private val allItemFlow: Flow<List<Item>> = allItemChannel
    private val cartItemPropertiesFlow: Flow<List<CartItemProperties>> = cartItemPropertiesChannel
    private val cartItemFlow: Flow<List<CartItem>> = cartItemChannel
    private val selectedCartItemFlow: Flow<List<CartItem>> = selectedCartItemChannel
    private val cartFlow: Flow<List<Cart>> = cartChannel

    override suspend fun save(vararg cartItemProperties: CartItemProperties) {
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
       return selectedCartItemFlow
    }

    override fun findAllCart(): Flow<List<Cart>> {
        return cartFlow
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

    override suspend fun getCartItemByItemId_internal(itemId: UUID): CartItemProperties? {
        return cartItemPropertiesTable.firstOrNull { itemId == it.itemId.id }
    }

    override suspend fun getItemByItemName(itemName: String): Item? {
        return this.itemTable.firstOrNull { it.name == itemName }
    }

    override fun getSelectedCart(): Cart? = this.cartTable.firstOrNull { it.selected }

    override suspend fun remove(cartItem: CartItemProperties) {
        cartItemPropertiesTable.remove(cartItem)
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

    private fun pushCartItems() {
        val cartItemJoinTable = getJoin(itemTable, cartItemPropertiesTable)
        externalScope.launch {
            cartItemChannel.value = cartItemJoinTable
            selectedCartItemChannel.value = cartItemJoinTable.filter { it.cartItemProperties.inCart == getSelectedCart()?.cartId }
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
        externalScope.launch {
            cartItemPropertiesChannel.value = cartItemPropertiesTable.toList()
            allItemChannel.value = itemTable.toList()
        }
    }

}
