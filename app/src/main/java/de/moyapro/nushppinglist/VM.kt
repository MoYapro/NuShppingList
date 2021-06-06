package de.moyapro.nushppinglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class VM : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    val coroutineScope = viewModelScope

    fun add(newItem: CartItem) {
        viewModelScope.launch {
            _cartItems.value += newItem
        }
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

