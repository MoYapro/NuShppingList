package de.moyapro.nushppinglist

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ViewModelTest {

    lateinit var viewModel: VM

    @Test
    fun getCurrentCart() {

    }

    fun getNotInCart() {

    }

    @Test
    fun addNewItemToCart() {
        val newItem = Item("bar")
        viewModel.add(newItem)
        assertEquals("Should have added item", 1, viewModel.cartItems)
    }

    fun setChecked() {

    }

    fun removeChecked() {

    }

    fun removeItem() {

    }

    fun deleteItem() {

    }


}
