package de.moyapro.nushppinglist

import de.moyapro.nushppinglist.mock.CartDaoMock
import de.moyapro.nushppinglist.util.MainCoroutineRule
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.*
import org.junit.Assert.*
import kotlin.random.Random

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
    private val cartDao: CartDaoMock =
        CartDaoMock(CoroutineScope(TestCoroutineDispatcher() + SupervisorJob()))

    @Before
    fun setup() {
        viewModel = VM(cartDao)
    }

    @After
    fun tearDown() {
        cartDao.reset()
    }

    @Test
    fun getCurrentCart() {

    }

    @Test
    fun getNotInCart() = runBlocking {
        val inCart = CartItem("in cart")
        val notInCart = Item("not in cart")
        viewModel.add(inCart)
        viewModel.add(notInCart)
        val notInCartItems = viewModel.nonCartItems.take(1).toList().flatten()
        assertEquals("Should have an item not in cart", 1, notInCartItems.size)
    }

    @Test
    fun addNewItem() = runBlocking {
        val newItem = Item("bar")
        viewModel.add(newItem)
        assertEquals("Should have added item to viewModel", 1, viewModel.allItems.value.size)

        val collectedList = cartDao.findAllItems().take(1).toList()
        assertEquals("Should have added item to database", newItem, collectedList[0][0])
    }

    @Test
    fun updateItem() = runBlocking {
        val newItem = Item("foo")
        viewModel.add(newItem)
        val updatedName = "bar"
        viewModel.update(newItem.copy(name = updatedName))
        assertEquals("Should have added item to viewModel", 1, viewModel.allItems.value.size)
        val updatedItem = cartDao.findAllItems().take(1).toList()[0][0]
        assertEquals("Should have updated name", updatedName, updatedItem.name)

    }


    @Test
    fun addNewItemToCart() = runBlocking {
        val newItem = CartItem("bar")
        viewModel.add(newItem)
        val allItems = cartDao.findAllInCart().take(1).toList().flatten()
        assertEquals("Should have added item to viewModel", 1, viewModel.cartItems.value.size)
        assertEquals("Should have added item to database", newItem.cartItemProperties, allItems[0])
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
    fun setChecked() = runBlocking {
        val item1 = CartItem("foo")
        val item2 = CartItem("bar")
        viewModel.add(item1)
        viewModel.add(item2)
        var cartItems = viewModel.cartItems.take(1).toList()[0]
        assertEquals("Should have two cartItems", 2, cartItems.size)
        assertTrue(
            "Nothing checked before",
            cartItems.none { it.checked })
        viewModel.toggleChecked(item1.cartItemProperties)
        cartItems = viewModel.cartItems.take(1).toList()[0]
        assertTrue(
            "Some are checked after checking one",
            cartItems.any { it.checked })
        assertTrue(
            "Some are NOT checked after checking one",
            cartItems.any { !it.checked })
        viewModel.toggleChecked(item2.cartItemProperties)
        cartItems = viewModel.cartItems.take(1).toList()[0]
        assertTrue(
            "All are checked after checking all items",
            cartItems.all { it.checked })
    }

    @Test
    fun setCheckedIsPersisted() = runBlocking {
        val item = CartItem("my item")
        viewModel.add(item)
        val itemsBeforeCheck = viewModel.cartItems.take(1).toList()[0]
        assertTrue("No item should be checked",
            itemsBeforeCheck.none { it.checked })
        viewModel.toggleChecked(item.cartItemProperties)
        val itemsAfterCheck = viewModel.cartItems.take(1).toList()[0]
        assertEquals("Should have one item in cart", 1, itemsAfterCheck.size)
        assertTrue(
            "All items should be checked",
            itemsAfterCheck.all { it.checked })
    }

    @Ignore("not implemented")
    @Test
    fun removeChecked() {
//        fail("foobar")
    }

    @Ignore("not implemented")
    fun removeItem() {

    }

    @Ignore("not implemented")
    fun deleteItem() {
    }

    @Test
    fun getAutocompleteItemsByFullName() {
        val item = Item("X")
        viewModel.add(item)
        val itemList = viewModel.getAutocompleteItems(item.name)
        assertEquals("Should find item", item.name, itemList[0])
    }

    @Test
    fun getItemByItemId() {
        val randomId = Random.nextLong()
        val itemInDb = Item(randomId, "ItemName")
        viewModel.add(itemInDb)
        val itemFromDb = viewModel.getItemByItemId(randomId)
        assertEquals("Should get item from DB", itemInDb, itemFromDb)
    }

    @Test
    fun getNoItemByItemId() {
        val itemInDb = Item(1, "ItemName")
        viewModel.add(itemInDb)
        val itemFromDb = viewModel.getItemByItemId(-1)
        assertNull("Should get NO item from DB", itemFromDb)
    }

    @Test
    fun addNewItemByName() = runBlocking {
        val itemName = "Milk"
        viewModel.addToCart(itemName)
        assertEquals(
            "Should have added item to cart",
            1,
            viewModel.cartItems.take(1).toList()[0].size
        )
    }

    @Test
    fun addExistingItemByName() = runBlocking {
        val itemName = "Milk"
        val newItem = Item(itemName)
        viewModel.add(newItem)
        viewModel.addToCart(itemName)
        val updatedCart = viewModel.cartItems.take(1).toList()[0]
        assertEquals("Should have added item to cart", 1, updatedCart.size)
        assertEquals("Cart should contain itemId", newItem.itemId, updatedCart[0].itemId)
    }

    @Test
    fun addExistingItemAsItem() = runBlocking {
        val newItem = Item("Itemname${Random.nextLong()}")
        viewModel.add(newItem)
        viewModel.addToCart(newItem)
        val updatedCart = viewModel.cartItems.take(1).toList()[0]
        assertEquals("Should have added item to cart", 1, updatedCart.size)
        assertEquals("Cart should contain itemId", newItem.itemId, updatedCart[0].itemId)
    }

    @Test
    fun removeCheckedFromCart() = runBlocking {
        viewModel.addToCart("one")
        viewModel.add(CartItem("checked one", checked = true))
        viewModel.addToCart("two")
        viewModel.add(CartItem("checked two", checked = true))

        viewModel.removeCheckedFromCart()

        val cartItems = viewModel.cartItems.take(1).toList().flatten()
        assertEquals("Should have two items in cart", 2, cartItems.size)
        assertTrue(
            "All cart items should be unchecked but was: $cartItems",
            cartItems.none { it.checked })
    }

    @Test
    fun getCartItemPropertiesForItem() = runBlocking {
        val cartItem = CartItem("thing")
        val (expectedCartItemProperties: CartItemProperties, item: Item) = cartItem
        viewModel.add(cartItem)
        val actualCartItemProperties = viewModel.getCartItemPropertiesByItemId(item.itemId)
        assertEquals(
            "Should find corect cartItemProperties for item",
            expectedCartItemProperties,
            actualCartItemProperties
        )
    }

    @Test
    fun updateCartItemProperties() = runBlocking {
        val itemId = Random.nextLong()
        val cartItemProperties = CartItemProperties(11, 12, itemId, 14, true)
        val cartItem = CartItem(
            cartItemProperties,
            Item(itemId, "x")
        )
        val expectedUpdated = CartItemProperties(11, 16, itemId, 18, false)
        viewModel.add(cartItem)
        assertEquals(
            "Should have saved original state",
            cartItemProperties,
            viewModel.getCartItemPropertiesByItemId(itemId)
        )
        viewModel.update(expectedUpdated)

        val cartItems = viewModel.cartItems.take(1).toList().flatten()
        assertEquals("Should still be one cart item but was $cartItems", 1, cartItems.size)
        assertEquals(
            "Should have updated saved state",
            expectedUpdated,
            viewModel.getCartItemPropertiesByItemId(itemId)
        )
    }

}
