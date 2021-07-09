package de.moyapro.nushppinglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.moyapro.nushppinglist.mock.CartDaoMock
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect

@FlowPreview
class VM(
    private val cartDao: CartDao
) : ViewModel() {

    constructor() : this(CartDaoMock(CoroutineScope(Dispatchers.IO + SupervisorJob())))

    val coroutineScope = viewModelScope
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems
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
                    val cartItemIds = _cartItems.value.map { cartItem -> cartItem.item.id }
                    _nonCartItems.value =
                        items.filter { item -> !cartItemIds.contains(item.id) }
                }
        }
    }

    fun add(newItem: Item) {
        cartDao.save(newItem)
    }

    fun update(newItem: Item) {
        cartDao.updateAll(newItem)
    }


    fun add(newItem: CartItem) {
        cartDao.save(newItem)
    }

    fun toggleChecked(itemToToggle: CartItem) {
        _cartItems.value = _cartItems.value.map { oldValue ->
            if (oldValue.item.id == itemToToggle.item.id) {
                oldValue
            } else {
                oldValue.copy(
                    cartItemProperties = oldValue.cartItemProperties.copy(checked = !oldValue.cartItemProperties.checked),
                    item = oldValue.item
                )
            }
        }
    }
}

