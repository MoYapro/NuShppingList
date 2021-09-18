package de.moyapro.nushppinglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import de.moyapro.nushppinglist.db.model.CartDao
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.CartItemProperties
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.mock.CartDaoMock
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect

@FlowPreview
class VM(
    private val cartDao: CartDao
) : ViewModel() {

    @Suppress("unused") // no-args constructor required by 'by viewmodels()'
    constructor() : this(CartDaoMock(CoroutineScope(Dispatchers.IO + SupervisorJob())))

    val coroutineScope = viewModelScope
    private val _cartItems = MutableStateFlow<List<CartItemProperties>>(emptyList())
    val cartItems: StateFlow<List<CartItemProperties>> = _cartItems
    private val _nonCartItems = MutableStateFlow<List<Item>>(emptyList())
    val nonCartItems: StateFlow<List<Item>> = _nonCartItems
    private val _allItems = MutableStateFlow<List<Item>>(emptyList())
    val allItems: StateFlow<List<Item>> = _allItems
    private val _allCartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val allCartItems: StateFlow<List<CartItem>> = _allCartItems

    private fun <T> MutableStateFlow<T>.listenTo(source: Flow<T>) {
        val that = this
        viewModelScope.launch {
            source.collect { valuesFromSource ->
                that.value = valuesFromSource
            }
        }
    }

    init {
        _cartItems.listenTo(cartDao.findAllInCart())
        _allItems.listenTo(cartDao.findAllItems())
        _allCartItems.listenTo(cartDao.findAllCartItems())

        viewModelScope.launch {
            cartDao.findAllItems()
                .collect { items ->
                    val cartItemIds = _cartItems.value.map { cartItem -> cartItem.itemId }
                    _nonCartItems.value =
                        items.filter { item -> !cartItemIds.contains(item.itemId) }
                }
        }
    }

    fun add(newItem: Item) {
        cartDao.save(newItem)
    }

    fun update(updatedItem: Item) {
        cartDao.updateAll(updatedItem)
    }

    fun update(updatedCartItemProperties: CartItemProperties) {
        cartDao.updateAll(updatedCartItemProperties)
    }

    @Transaction
    fun add(newItem: CartItem) {
        cartDao.save(newItem.item)
        cartDao.save(newItem.cartItemProperties)
    }

    fun toggleChecked(itemToToggle: CartItemProperties) {
        _cartItems.value = _cartItems.value.map { oldValue ->
            if (oldValue.itemId == itemToToggle.itemId) {
                val updated = oldValue.copy(
                    checked = !oldValue.checked
                )
                this.cartDao.updateAll(updated)
                updated
            } else {
                oldValue
            }
        }
    }

    fun getItemByItemId(itemId: Long): Item? {
        return cartDao.getItemByItemId(itemId)
    }

    fun getAutocompleteItems(searchString: String): List<String> {
        return nonCartItems.value
            .filter { matched(it.name, searchString) }
            .map { it.name }
    }

    fun addToCart(item: Item) {
        val existingCartItem: CartItemProperties? =
            cartDao.getCartItemByItemId(item.itemId)
        if (null == existingCartItem) {
            add(CartItem(item))
        } else {
            val updatedCartItemProperties =
                existingCartItem.copy(amount = existingCartItem.amount + 1)
            update(updatedCartItemProperties)
        }
    }


    fun addToCart(itemName: String) {
        val existingItem: Item? = cartDao.getItemByItemName(itemName)
        if (null == existingItem) {
            add(CartItem(itemName))
        } else {
            add(CartItem(existingItem))
        }
    }

    fun removeCheckedFromCart() {
        cartItems.value
            .filter { it.checked }
            .forEach { cartItem ->
                cartDao.remove(cartItem)
            }
    }

    fun getCartItemPropertiesByItemId(itemId: Long): CartItemProperties? {
        return cartDao.getCartItemByItemId(itemId)
    }


}

fun matched(name: String, searchString: String): Boolean {
    return name.contains(searchString, true)
}

