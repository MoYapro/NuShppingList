package de.moyapro.nushppinglist

import de.moyapro.nushppinglist.util.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@ExperimentalCoroutinesApi
class ViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

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

    @Test
    fun subscriberGetsNotifiedWhenItemIsAdded() {
        var valueChanged = false
        viewModel.coroutineScope.launch {
            viewModel.cartItems.collect { currentItemList ->
                if (currentItemList.isNotEmpty()) {
                    valueChanged = true
                }
            }
        }
        assertFalse("Value should NOT have changed", valueChanged)
        viewModel.add(Item("bar"))
        assertTrue("Value should have changed", valueChanged)
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
