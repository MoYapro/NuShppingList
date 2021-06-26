package de.moyapro.nushppinglist

import de.moyapro.nushppinglist.util.MainCoroutineRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
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

    private lateinit var viewModel: VM
    private val cartDao: CartDao =
        CartDaoMock(CoroutineScope(TestCoroutineDispatcher() + SupervisorJob()))

    @Before
    fun setup() {
        viewModel = VM(cartDao)
    }

    @Test
    fun getCurrentCart() {

    }

    @Ignore("not implemented")
    @Test
    fun getNotInCart() {
    }

    @Test
    fun addNewItemToCart() {
        val newItem = CartItem("bar")
        viewModel.add(newItem)
        assertEquals("Should have added item to viewModel", 1, viewModel.cartItems.value.size)
        viewModel.coroutineScope.launch {
            cartDao.findAll().collect { collectedList ->
                assertEquals("Should have added item to database", 1, collectedList)
            }
        }
    }

    @Test
    fun addNewItemButNotToCart() {
        val newItem = Item("bar")
        viewModel.add(newItem)
        assertEquals("Should have added item", 0, viewModel.cartItems.value.size)
        assertEquals("Item should be persisted", 1, cartDao.findNotAddedItems().size)
    }

    @Ignore("not implemented")
    @Test
    fun removeItemFromCart() {

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
            viewModel.cartItems.value.any { it.cartItemProperties.checked })
        assertTrue(
            "Some are NOT checked after checking one",
            viewModel.cartItems.value.any { !it.cartItemProperties.checked })
        viewModel.toggleChecked(item2)
        assertTrue(
            "All are checked after checking all items",
            viewModel.cartItems.value.all { it.cartItemProperties.checked })
    }

    @Ignore("not implemented")
    @Test
    fun setCheckedIsPersisted() {
        val item = CartItem("my item")
        var itemCollected = false
        viewModel.add(item)
        viewModel.coroutineScope.launch {
            cartDao.findAll().collect { collectedItems ->
                assertEquals("Should have one item in cart", 1, collectedItems.size)
                assertTrue("No item should be checked", collectedItems.none { cartItem ->
                    cartItem.cartItemProperties.checked
                }
                )
                itemCollected = true
            }
        }

        viewModel.toggleChecked(item)

        viewModel.coroutineScope.launch {
            cartDao.findAll().collect { collectedItems ->
                assertEquals("Should have one item in cart", 1, collectedItems.size)
                assertTrue("No item should be checked", collectedItems.all { cartItem ->
                    cartItem.cartItemProperties.checked
                }
                )
            }
        }
        assertTrue("Should have collected an item", itemCollected)
    }

    @Ignore("not implemented")
    fun removeChecked() {

    }

    @Ignore("not implemented")
    fun removeItem() {

    }

    @Ignore("not implemented")
    fun deleteItem() {

    }


}
