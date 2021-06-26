package de.moyapro.nushppinglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class VM(
    private val cartDao: CartDao
) : ViewModel() {

    val coroutineScope = viewModelScope
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    init {
        viewModelScope.launch {
            cartDao.findAll()
                .collect { cartItems ->
                    // make DB values available for the UI
                    _cartItems.value = cartItems
                }
        }
    }

    fun add(newItem:Item) {
        cartDao.save(newItem)
    }


    fun add(newItem: CartItem) {
//        viewModelScope.launch {
//            _cartItems.value += newItem
//        }
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

