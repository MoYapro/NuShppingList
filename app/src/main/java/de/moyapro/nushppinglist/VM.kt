package de.moyapro.nushppinglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import de.moyapro.nushppinglist.mock.CartDaoMock
import kotlinx.coroutines.*
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

    init {
        // subscribe to DB changes to make them visible to the UI
        viewModelScope.launch {
            cartDao.findAllInCart()
                .collect { cartItems ->
                    _cartItems.value = cartItems
                }
        }
        viewModelScope.launch {
            cartDao.findAllItems()
                .collect { items ->
                    _allItems.value = items
                }
        }
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

    fun update(newItem: Item) {
        cartDao.updateAll(newItem)
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

    fun getAutocompleteItems(searchString: String): List<Item> {
        return nonCartItems.value.filter { matched(it.name, searchString) }
    }

}

fun matched(name: String, searchString: String): Boolean {
    return name.contains(searchString, true)
}

