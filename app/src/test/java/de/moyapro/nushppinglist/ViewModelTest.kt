package de.moyapro.nushppinglist

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ViewModelTest {

    lateinit var viewModel: VM

    @Before
    fun setup() {
        viewModel = VM()
    }

    @Test
    fun getCurrentCart() {

    }

    fun getNotInCart() {

    }

    @Test
    fun addNewItemToCart() {
        val newItem = Item("bar")
        viewModel.add(newItem)
        assertEquals("Should have added item", 1, viewModel.cartItems.value.size)
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
