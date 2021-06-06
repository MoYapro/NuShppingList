package de.moyapro.nushppinglist

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class VM : ViewModel() {

    val mycoroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    val cartItems = MutableStateFlow<List<Item>>(emptyList())

    fun add(newItem: Item) {
        runBlocking {
            launch {
                cartItems.value += newItem
            }
        }
    }
}

