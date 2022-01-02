package de.moyapro.nushppinglist.mock

import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.dao.getItemByItemId
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.CartItemProperties
import de.moyapro.nushppinglist.db.model.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.*

@FlowPreview
class CartDaoMock(
    private val externalScope: CoroutineScope,
) : CartDao {

    private val itemTable: MutableSet<Item> = mutableSetOf()
    private val cartItemPropertiesTable: MutableSet<CartItemProperties> = mutableSetOf()

    private val cartItemChannel: MutableStateFlow<List<CartItem>> = MutableStateFlow(listOf())
    private val cartItemPropertiesChannel: MutableStateFlow<List<CartItemProperties>> =
        MutableStateFlow(listOf())
    private val allItemChannel: MutableStateFlow<List<Item>> = MutableStateFlow(listOf())
    private val allItemFlow: Flow<List<Item>> = allItemChannel
    private val cartItemPropertiesFlow: Flow<List<CartItemProperties>> = cartItemPropertiesChannel
    private val cartItemFlow: Flow<List<CartItem>> = cartItemChannel

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
        val updatedItemTable: List<CartItemProperties> =
            cartItemPropertiesTable.map { itemFromTable ->
                if (toUpdate.containsKey(itemFromTable.itemId)) {
                    toUpdate[itemFromTable.itemId]!!
                } else {
                    itemFromTable
                }
            }
        cartItemPropertiesTable.clear()
        cartItemPropertiesTable.addAll(updatedItemTable.toSet())
        pushCartItemProperties()
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

    override suspend fun getCartItemByItemId_internal(itemId: UUID): CartItemProperties? {
        return cartItemPropertiesTable.firstOrNull { itemId == it.itemId.id }
    }

    override suspend fun getItemByItemName(itemName: String): Item? {
        return this.itemTable.firstOrNull { it.name == itemName }
    }

    override suspend fun remove(cartItem: CartItemProperties) {
        cartItemPropertiesTable.remove(cartItem)
        pushCartItemProperties()

    }

    override suspend fun remove(item: Item) {
        itemTable.remove(item)
        pushItems()
    }

    private fun pushCartItems() {
        val cartItemJoinTable = getJoin(itemTable, cartItemPropertiesTable)
        externalScope.launch {
            cartItemChannel.value = cartItemJoinTable
        }
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
