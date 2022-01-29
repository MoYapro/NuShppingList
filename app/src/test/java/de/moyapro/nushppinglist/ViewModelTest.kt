package de.moyapro.nushppinglist

import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.db.model.Cart
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.CartItemProperties
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.mock.CartDaoMock
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.util.createSampleRecipeCake
import de.moyapro.nushppinglist.ui.util.createSampleRecipeItem
import de.moyapro.nushppinglist.ui.util.createSampleRecipeNoodels
import de.moyapro.nushppinglist.util.MainCoroutineRule
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds


@Suppress("EXPERIMENTAL_API_USAGE")
@ExperimentalCoroutinesApi
class ViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var viewModel: CartViewModel
    private val cartDao: CartDaoMock =
        CartDaoMock(CoroutineScope(TestCoroutineDispatcher() + SupervisorJob()))

    @Before
    fun setup() {
        viewModel = CartViewModel(cartDao)
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
        Thread.sleep(100)
        val notInCartItems = viewModel.nonCartItems.take(1).toList().flatten()
        assertEquals("Should have an item not in cart", 1, notInCartItems.size)
    }

    @Test
    fun addNewItem() = runBlocking {
        val newItem = Item("bar")
        viewModel.add(newItem)
        Thread.sleep(100)
        assertEquals("Should have added item to viewModel", 1, viewModel.allItems.value.size)

        val collectedList = cartDao.findAllItems().take(1).toList()
        assertEquals("Should have added item to database", newItem, collectedList[0][0])
    }

    @Test
    fun subtractItemFromCart() = runBlocking {
        val newItem = Item("bar")
        val timesAdded = 3
        repeat(timesAdded) {
            viewModel.addToCart(newItem)
        }
        Thread.sleep(100)

        viewModel.cartItems.take(1).first().first().amount shouldBe timesAdded

        viewModel.subtractFromCart(newItem.itemId)
        Thread.sleep(100)
        viewModel.cartItems.take(1).first().first().amount shouldBe timesAdded - 1
        viewModel.subtractFromCart(newItem.itemId)
        Thread.sleep(100)
        viewModel.cartItems.take(1).first().first().amount shouldBe timesAdded - 2
    }

    @Test
    fun updateItem() = runBlocking {
        val newItem = Item("foo")
        viewModel.add(newItem)
        Thread.sleep(100)
        val updatedName = "bar"
        viewModel.update(newItem.copy(name = updatedName))
        Thread.sleep(100)
        assertEquals("Should have added item to viewModel", 1, viewModel.allItems.value.size)
        val updatedItem = cartDao.findAllItems().take(1).toList()[0][0]
        assertEquals("Should have updated name", updatedName, updatedItem.name)

    }


    @Test
    fun addNewItemToCart() = runBlocking {
        val newItem = CartItem("bar")
        viewModel.add(newItem)
        Thread.sleep(100)
        val allItems = cartDao.findAllInCart().take(1).toList().flatten()
        assertEquals("Should have added item to viewModel", 1, viewModel.cartItems.value.size)
        assertEquals("Should have added item to database", newItem.cartItemProperties, allItems[0])
    }

    @Test
    fun addNewItemButNotToCart() {
        val newItem = Item("bar")
        viewModel.add(newItem)
        Thread.sleep(100)
        assertEquals("Should have added item", 0, viewModel.cartItems.value.size)
        assertEquals("Item should be persisted", 1, cartDao.findNotAddedItems().size)
    }

    @Test
    fun removeItemFromCart() = runBlocking {
        val itemToRemove = Item("remove me")
        viewModel.add(CartItem(itemToRemove).apply { cartItemProperties.checked = true })

        viewModel.removeCheckedFromCart()
        Thread.sleep(100)

        viewModel.getItemByItemId(itemToRemove.itemId) shouldBe itemToRemove
        viewModel.getCartItemPropertiesByItemId(itemToRemove.itemId) shouldBe null
        viewModel.cartItems.take(1).first() shouldBe emptyList()
    }

    @Test
    fun setChecked() = runBlocking {
        val item1 = CartItem("foo")
        val item2 = CartItem("bar")
        viewModel.add(item1)
        viewModel.add(item2)
        Thread.sleep(100)
        var cartItems = viewModel.cartItems.take(1).toList()[0]
        assertEquals("Should have two cartItems", 2, cartItems.size)
        assertTrue(
            "Nothing checked before",
            cartItems.none { it.checked })
        viewModel.toggleChecked(item1.cartItemProperties)
        Thread.sleep(100)
        cartItems = viewModel.cartItems.take(1).toList()[0]
        assertTrue(
            "Some are checked after checking one",
            cartItems.any { it.checked })
        assertTrue(
            "Some are NOT checked after checking one",
            cartItems.any { !it.checked })
        viewModel.toggleChecked(item2.cartItemProperties)
        Thread.sleep(100)
        cartItems = viewModel.cartItems.take(1).toList()[0]
        assertTrue(
            "All are checked after checking all items",
            cartItems.all { it.checked })
    }

    @Test
    fun setCheckedIsPersisted() = runBlocking {
        val item = CartItem("my item")
        viewModel.add(item)
        Thread.sleep(100)
        val itemsBeforeCheck = viewModel.cartItems.take(1).toList()[0]
        assertTrue("No item should be checked",
            itemsBeforeCheck.none { it.checked })
        viewModel.toggleChecked(item.cartItemProperties)
        Thread.sleep(100)
        val itemsAfterCheck = viewModel.cartItems.take(1).toList()[0]
        assertEquals("Should have one item in cart", 1, itemsAfterCheck.size)
        assertTrue(
            "All items should be checked",
            itemsAfterCheck.all { it.checked })
    }

    @Test
    fun getAutocompleteItemsByFullName() {
        val item = Item("X")
        viewModel.add(item)
        Thread.sleep(100)
        val itemList = viewModel.getAutocompleteItems(item.name)
        assertEquals("Should find item", item.name, itemList[0])
    }

    @Test
    fun getItemByItemId() {
        val randomId = ItemId()
        val itemInDb = Item("ItemName", randomId)
        viewModel.add(itemInDb)
        Thread.sleep(100)
        val itemFromDb = viewModel.getItemByItemId(randomId)
        assertEquals("Should get item from DB", itemInDb, itemFromDb)
    }

    @Test
    fun getNoItemByItemId() {
        val uuid1 = UUID.randomUUID()
        val uuid2 = UUID.randomUUID()
        val itemInDb = Item("ItemName", ItemId(uuid1))
        viewModel.add(itemInDb)
        Thread.sleep(100)
        val itemFromDb = viewModel.getItemByItemId(ItemId(uuid2))
        assertNull("Should get NO item from DB", itemFromDb)
    }

    @Test
    fun getAllCartItems() = runBlocking {
        val name = "newItemInCart"
        viewModel.add(CartItem(name))
        Thread.sleep(100)
        viewModel.allCartItems.take(1).toList()[0]
        val cartItem = viewModel.allCartItems.take(1).toList()[0]
        assertEquals("Should get cartItem with given name", name, cartItem[0].item.name)
        assertEquals(
            "Ids of cartItem and item should match",
            cartItem[0].cartItemProperties.itemId,
            cartItem[0].item.itemId
        )
    }

    @Test
    fun addNewItemByName() = runBlocking {
        val itemName = "Milk"
        viewModel.addToCart(itemName)
        Thread.sleep(100)
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
        Thread.sleep(100)
        val updatedCart = viewModel.cartItems.take(1).toList()[0]
        assertEquals("Should have added item to cart", 1, updatedCart.size)
        assertEquals("Cart should contain itemId", newItem.itemId, updatedCart[0].itemId)
    }

    @Test
    fun addExistingItemAsItem() = runBlocking {
        val newItem = Item("Itemname${Random.nextLong()}")
        viewModel.add(newItem)
        viewModel.addToCart(newItem)
        Thread.sleep(100)
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
        Thread.sleep(100)

        viewModel.removeCheckedFromCart()

        Thread.sleep(100)
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
        Thread.sleep(100)
        val actualCartItemProperties = viewModel.getCartItemPropertiesByItemId(item.itemId)
        assertEquals(
            "Should find corect cartItemProperties for item",
            expectedCartItemProperties,
            actualCartItemProperties
        )
    }

    @Test
    fun updateCartItemProperties() = runBlocking {
        val itemId = ItemId()
        val cartItemProperties = CartItemProperties(newItemId = itemId).apply { checked = true }
        val cartItem = CartItem(
            cartItemProperties,
            Item("x", itemId)
        )
        val expectedUpdated = cartItemProperties.copy(checked = false)
        viewModel.add(cartItem)
        Thread.sleep(100)
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

    @Test
    fun addRecipeItemToCart() = runBlocking {
        val recipeItem = createSampleRecipeItem()
        viewModel.addToCart(recipeItem)
        Thread.sleep(100)

        val cartItems = viewModel.cartItems.take(1).toList().flatten()
        cartItems.map { it.itemId } shouldContainExactly listOf(recipeItem.item.itemId)
        cartItems.map { it.recipeId }.toSet() shouldContainExactly setOf(recipeItem.recipeId)
    }

    @Test
    fun addRecipeToCart(): Unit = runBlocking {
        val recipeToAddToCart = createSampleRecipeCake()

        viewModel.addRecipeToCart(recipeToAddToCart)
        Thread.sleep(100)

        val cartItems = viewModel.cartItems.take(1).toList().flatten()
        cartItems.map { it.itemId } shouldContainExactlyInAnyOrder recipeToAddToCart.recipeItems.map { it.item.itemId }
    }

    @Test
    fun getCartGroupedByRecipe(): Unit = runBlocking {
        val recipeCake = createSampleRecipeCake()
        val recipeNoodels = createSampleRecipeNoodels()
        viewModel.addRecipeToCart(recipeCake)
        viewModel.addRecipeToCart(recipeNoodels)
        viewModel.addToCart("Vanilla Coke")
        Thread.sleep(100)

        val cartItemsGrouped = viewModel.allCartItemsGrouped.take(1).toList().single()
        cartItemsGrouped[null]!! shouldHaveSize 1
        cartItemsGrouped[recipeCake.recipeId]!! shouldHaveSize recipeCake.recipeItems.size
        cartItemsGrouped[recipeNoodels.recipeId]!! shouldHaveSize recipeNoodels.recipeItems.size
    }

    @Test
    fun deleteItem() = runBlocking {
        val itemToRemove = Item("Dubiose Matsche")
        val recipe = createSampleRecipeCake()
        recipe.recipeItems = listOf(createSampleRecipeItem().apply { item = itemToRemove })
        viewModel.addToCart(itemToRemove)

        viewModel.removeItem(itemToRemove)
        Thread.sleep(100)
        viewModel.getItemByItemId(itemToRemove.itemId) shouldBe null
        viewModel.getCartItemPropertiesByItemId(itemToRemove.itemId) shouldBe null
    }

    @Test
    fun createNewCart() = runBlocking {
        val cart = Cart()
        viewModel.add(cart)
        Thread.sleep(100)
        val carts = viewModel.allCart.take(1).toList().flatten()
        carts.single() shouldBe cart
    }

    @Test
    fun updateCart() = runBlocking {
        val cart = Cart()
        viewModel.add(cart)
        Thread.sleep(100)
        val carts = viewModel.allCart.take(1).toList().flatten()
        carts.single() shouldBe cart

        val updatedCart = cart.copy(cartName = "newName", synced = !cart.synced)
        viewModel.update(updatedCart)
        Unit
    }

    @Test
    fun deleteCart() = runBlocking {
        val cart = Cart()
        viewModel.add(cart)
        Thread.sleep(100)
        val carts = viewModel.allCart.take(1).toList().flatten()
        carts.single() shouldBe cart

        viewModel.removeCart(cart)
        Unit
    }


    @Test
    fun getSelectedCart() = runBlocking {
        val selectedCart = Cart(cartName = "Selected").apply { selected = true }
        val notSelectedCart = Cart(cartName = "Not selected").apply { selected = false }
        viewModel.add(selectedCart)
        viewModel.add(notSelectedCart)
        delay(1.seconds)
        val result = viewModel.getSelectedCart()

        result shouldBe selectedCart

    }

    @Test
    fun setSelectedCart() = runBlocking {
        val initialySelected = Cart().apply { selected = true }
        val eventuallySelected = Cart().apply { selected = false }
        viewModel.add(initialySelected)
        viewModel.add(eventuallySelected)
        delay(1.seconds)

        viewModel.selectCart(eventuallySelected)
        delay(1.seconds)

        val carts: List<Cart> = viewModel.allCart.take(1).first()

        viewModel.getSelectedCart() shouldBe eventuallySelected
        carts.single { it == eventuallySelected }.selected shouldBe true
        carts.single { it == initialySelected }.selected shouldBe false
    }

    @Test
    fun getItemsInSpecificCart() = runBlocking {
        val cart1 = Cart().apply { selected = false }
        val cart2 = Cart().apply { selected = false }
        val cart1Item1 = CartItem(Item()).apply { cartItemProperties.inCart = cart1.cartId }
        val cart1Item2 = CartItem(Item()).apply { cartItemProperties.inCart = cart1.cartId }
        val cart2Item1 = CartItem(Item()).apply { cartItemProperties.inCart = cart2.cartId }
        val cart2Item2 = CartItem(Item()).apply { cartItemProperties.inCart = cart2.cartId }
        viewModel.add(cart1)
        viewModel.add(cart2)
        viewModel.add(cart1Item1)
        viewModel.add(cart1Item2)
        viewModel.add(cart2Item1)
        viewModel.add(cart2Item2)
        delay(100.milliseconds)
        // no cart selected
        val emptyCart = viewModel.allCartItemsGrouped.take(1).first()
        emptyCart shouldBe emptyMap()

        viewModel.selectCart(cart1)
        delay(100.milliseconds)
        val cart1Items = viewModel.allCartItemsGrouped.take(1).first().values.flatten()
        cart1Items shouldContainExactlyInAnyOrder listOf(cart1Item1, cart1Item2)

        viewModel.selectCart(cart2)
        delay(100.milliseconds)
        val cart2Items = viewModel.allCartItemsGrouped.take(1).first().values.flatten()
        cart2Items shouldContainExactlyInAnyOrder listOf (cart2Item1, cart2Item2)

        Unit
    }

}
