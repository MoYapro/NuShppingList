package de.moyapro.nushppinglist

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

class VM: ViewModel() {
    val cartItems: StateFlow<List<Item>> = state

    fun add(newItem: Item) {
        TODO("Not yet implemented")
    }


}
