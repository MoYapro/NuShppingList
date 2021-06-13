package de.moyapro.nushppinglist

import de.moyapro.nushppinglist.util.MainCoroutineRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
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
        viewModel = VM(CartDaoMock(MainScope()))
    }

    @Test
    fun getCurrentCart() {

    }

    fun getNotInCart() {

    }

    @Test
    fun addNewItemToCart() {
        val newItem = CartItem("bar")
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
        viewModel.add(CartItem("bar"))
        assertTrue("Value should have changed", valueChanged)
    }

    @Test
    fun setChecked() {
        val item1 = CartItem("foo")
        val item2 = CartItem("bar")
        viewModel.add(item1)
        viewModel.add(item2)
        assertTrue(
            "Nothing checked before",
            viewModel.cartItems.value.none { it.cartItemProperties.checked })
        viewModel.toggleChecked(item1)
        assertTrue(
            "Some are checked after checking one",
            viewModel.cartItems.value.any() { it.cartItemProperties.checked })
        assertTrue(
            "Some are NOT checked after checking one",
            viewModel.cartItems.value.any() { !it.cartItemProperties.checked })
        viewModel.toggleChecked(item2)
        assertTrue(
            "All are checked after checking all items",
            viewModel.cartItems.value.all { it.cartItemProperties.checked })

    }

    fun removeChecked() {

    }

    fun removeItem() {

    }

    fun deleteItem() {

    }


}
