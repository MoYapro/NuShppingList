package de.moyapro.nushppinglist

import android.util.Log
import de.moyapro.nushppinglist.constants.CONSTANTS.DEFAULT_CART
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
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.*
import org.junit.Assert.*
import java.util.*
import kotlin.random.Random


@Suppress("EXPERIMENTAL_API_USAGE")
@ExperimentalCoroutinesApi
class ViewModelTest {

    private val tag = ViewModelTest::class.simpleName

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var viewModel: CartViewModel
    private val cartDao: CartDaoMock =
        CartDaoMock(CoroutineScope(TestCoroutineDispatcher() + SupervisorJob()))

    @Before
    fun setup() {
        cartDao.reset()
        viewModel = CartViewModel(cartDao)
        Thread.sleep(100)
    }

    @After
    fun tearDown() {
        cartDao.reset()
    }

    @Test
    fun getCurrentCart() {

    }

    @Test
    fun addNewItem(): Unit = runBlocking {
        val newItem = Item("bar")
        viewModel.add(newItem)
        Thread.sleep(100)
        assertEquals("Should have added item to viewModel", 1, viewModel.allItems.value.size)

        val collectedList = cartDao.findAllItems().take(1).toList()
        assertEquals("Should have added item to database", newItem, collectedList[0][0])
    }

    @Test
    fun subtractItemFromCart(): Unit = runBlocking {
        val newItem = Item("bar")
        val timesAdded = 3
        viewModel.add(DEFAULT_CART)
        repeat(timesAdded) {
            viewModel.addToCart(newItem)
            Thread.sleep(100)
        }

        cartDao.cartItemPropertiesTable.single().amount shouldBe timesAdded

        viewModel.subtractFromCart(newItem.itemId)
        Thread.sleep(100)
        cartDao.cartItemPropertiesTable.single().amount shouldBe timesAdded - 1
        viewModel.subtractFromCart(newItem.itemId)
        Thread.sleep(100)
        cartDao.cartItemPropertiesTable.single().amount shouldBe timesAdded - 2
    }

    @Test
    fun updateItem(): Unit = runBlocking {
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
    fun addNewItemToCart(): Unit = runBlocking {
        val newItem = CartItem("bar", DEFAULT_CART.cartId)
        viewModel.add(DEFAULT_CART)
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
    fun removeCheckedItemFromCart(): Unit = runBlocking {
        val itemToRemove = Item("remove me")
        viewModel.add(DEFAULT_CART)
        viewModel.add(CartItem(itemToRemove,
            DEFAULT_CART.cartId).apply { cartItemProperties.checked = true })
        Thread.sleep(100)
        cartDao.cartItemPropertiesTable.single().checked shouldBe true
        viewModel.removeCheckedFromCart()
        Thread.sleep(100)
        viewModel.getItemByItemId(itemToRemove.itemId) shouldBe itemToRemove
        viewModel.getCartItemPropertiesByItemId(itemToRemove.itemId) shouldBe null
        cartDao.cartItemPropertiesTable shouldBe emptySet()
    }

    @Test
    fun setChecked(): Unit = runBlocking {
        val item1 = CartItem("foo", DEFAULT_CART.cartId)
        val item2 = CartItem("bar", DEFAULT_CART.cartId)
        viewModel.add(DEFAULT_CART)
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

        viewModel.toggleChecked(item1.cartItemProperties)
        viewModel.toggleChecked(item2.cartItemProperties)
        Thread.sleep(100)
        assertTrue("All are unchecked after toggeling again",
            cartItems.all { !it.checked })
    }

    @Test
    fun setCheckedIsPersisted(): Unit = runBlocking {
        val item = CartItem("my item", DEFAULT_CART.cartId)
        viewModel.add(DEFAULT_CART)
        viewModel.add(item)
        Thread.sleep(100)
        cartDao.cartItemPropertiesTable.single().checked shouldBe false
        viewModel.toggleChecked(item.cartItemProperties)
        Thread.sleep(100)
        cartDao.cartItemPropertiesTable.single().checked shouldBe true
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
    fun getAllCartItems(): Unit = runBlocking {
        val name = "newItemInCart"
        viewModel.add(DEFAULT_CART)
        viewModel.add(CartItem(name, DEFAULT_CART.cartId))
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
    fun addNewItemByName(): Unit = runBlocking {
        val itemName = "Milk"
        viewModel.add(DEFAULT_CART)
        viewModel.addToCart(itemName)
        Thread.sleep(100)
        val item = cartDao.itemTable.single()
        val cartItemProperties = cartDao.cartItemPropertiesTable.single()
        item.name shouldBe itemName
        cartItemProperties.itemId shouldBe item.itemId
        cartItemProperties.amount shouldBe item.defaultItemAmount
    }

    @Test
    fun addExistingItemByName(): Unit = runBlocking {
        val itemName = "Milk"
        val newItem = Item(itemName)
        viewModel.add(DEFAULT_CART)
        viewModel.add(newItem)
        viewModel.addToCart(itemName)
        Thread.sleep(100)
        with(cartDao.itemTable.single()) {
            this.name shouldBe itemName
            this.itemId shouldBe newItem.itemId
        }
    }

    @Test
    fun addExistingItemAsItem(): Unit = runBlocking {
        val newItem = Item("Itemname${Random.nextLong()}")
        viewModel.add(DEFAULT_CART)
        viewModel.add(newItem)
        repeat(10) { i ->
            viewModel.addToCart(newItem)
            Thread.sleep(100)
            with(viewModel.cartItems.take(1).toList().flatten().single()) {
                this.itemId shouldBe newItem.itemId
                this.amount shouldBe (1 + i)
            }
        }
    }

    @Test
    fun addExistingItemAsItemMultiple(): Unit = runBlocking {
        val repetitions = 99
        val newItem = Item("Itemname${Random.nextLong()}")
        viewModel.add(DEFAULT_CART)
        viewModel.add(newItem)
        run {
            repeat(repetitions) {
                viewModel.addToCart(newItem)
                Thread.sleep(10)
            }
        }
        with(viewModel.cartItems.take(1).toList().flatten().single()) {
            this.itemId shouldBe newItem.itemId
            this.amount shouldBe repetitions
        }
        cartDao.cartItemPropertiesTable shouldHaveSize 1
    }

    @Test
    fun removeCheckedFromCart(): Unit = runBlocking {
        viewModel.add(DEFAULT_CART)
        viewModel.addToCart("one")
        viewModel.add(CartItem("checked one", DEFAULT_CART.cartId, checked = true))
        viewModel.addToCart("two")
        viewModel.add(CartItem("checked two", DEFAULT_CART.cartId, checked = true))
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
    fun getCartItemPropertiesForItem(): Unit = runBlocking {
        val cartItem = CartItem("thing", DEFAULT_CART.cartId)
        val (expectedCartItemProperties: CartItemProperties, item: Item) = cartItem
        viewModel.add(DEFAULT_CART)
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
    fun updateCartItemProperties(): Unit = runBlocking {
        val itemId = ItemId()
        val originalCartItemProperties =
            CartItemProperties(newItemId = itemId, DEFAULT_CART.cartId).apply { checked = true }
        val cartItem = CartItem(originalCartItemProperties, Item("x", itemId))
        val updatedCartItemProperties = originalCartItemProperties.copy(checked = false)
        viewModel.add(DEFAULT_CART)
        viewModel.add(cartItem)
        Thread.sleep(100)
        viewModel.getCartItemPropertiesByItemId(itemId) shouldBe originalCartItemProperties
        viewModel.update(updatedCartItemProperties)
        Thread.sleep(100)
        viewModel.getCartItemPropertiesByItemId(itemId) shouldBe updatedCartItemProperties
        cartDao.cartItemPropertiesTable shouldHaveSize 1
    }

    @Test
    fun addRecipeItemToCart(): Unit = runBlocking {
        val recipeItem = createSampleRecipeItem()
        viewModel.add(DEFAULT_CART)
        viewModel.addToCart(recipeItem)
        Thread.sleep(100)

        val cartItems = viewModel.cartItems.take(1).toList().flatten()
        cartItems.map { it.itemId } shouldContainExactly listOf(recipeItem.item.itemId)
        cartItems.map { it.recipeId }.toSet() shouldContainExactly setOf(recipeItem.recipeId)
    }

    @Test
    fun addRecipeToCart(): Unit = runBlocking {
        val recipeToAddToCart = createSampleRecipeCake()
        viewModel.add(DEFAULT_CART)
        viewModel.addRecipeToCart(recipeToAddToCart)
        Thread.sleep(100)

        val cartItems = viewModel.cartItems.take(1).toList().flatten()
        cartItems.map { it.itemId } shouldContainExactlyInAnyOrder recipeToAddToCart.recipeItems.map { it.item.itemId }
    }

    @Test
    @Ignore("recipe not implemented yet")
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
    fun deleteItem(): Unit = runBlocking {
        val itemToRemove = Item("Dubiose Matsche")
        val recipe = createSampleRecipeCake()
        recipe.recipeItems = listOf(createSampleRecipeItem().apply { item = itemToRemove })
        viewModel.addToCart(itemToRemove)
        Thread.sleep(100)

        viewModel.removeItem(itemToRemove)
        Thread.sleep(100)
        viewModel.getItemByItemId(itemToRemove.itemId) shouldBe null
        viewModel.getCartItemPropertiesByItemId(itemToRemove.itemId) shouldBe null
    }

    @Test
    fun createNewCart(): Unit = runBlocking {
        val cart = Cart()
        viewModel.add(cart)
        Thread.sleep(100)
        val carts = viewModel.allCart.take(1).toList().flatten()
        carts.single { it.cartId != DEFAULT_CART.cartId } shouldBe cart
    }

    @Test
    fun updateCart(): Unit = runBlocking {
        val cart = Cart()
        viewModel.add(cart)
        Thread.sleep(100)
        val carts = viewModel.allCart.take(1).toList().flatten()
        carts.single { it.cartId != DEFAULT_CART.cartId } shouldBe cart

        val updatedCart = cart.copy(cartName = "newName", synced = !cart.synced, selected = true)
        viewModel.update(updatedCart)
        Thread.sleep(100)
        cartDao.cartTable.single { it.cartId != DEFAULT_CART.cartId } shouldBe updatedCart
    }

    @Test
    fun deleteCart(): Unit = runBlocking {
        val cart = Cart()
        viewModel.add(cart)
        Thread.sleep(100)
        val carts = viewModel.allCart.take(1).toList().flatten()
        carts.single { it.cartId != DEFAULT_CART.cartId } shouldBe cart

        viewModel.removeCart(cart)
    }


    @Test
    fun getSelectedCart(): Unit = runBlocking {
        val selectedCart = Cart(cartName = "Selected").apply { selected = true }
        val notSelectedCart = Cart(cartName = "Not selected").apply { selected = false }
        viewModel.add(notSelectedCart)
        viewModel.add(selectedCart)
        Thread.sleep(100)
        println(cartDao.cartTable.map { "${it.cartName} ${it.selected}" })
        cartDao.cartTable.filter { it.selected == true }[0] shouldBe selectedCart
        cartDao.getSelectedCart() shouldBe selectedCart
        val result = viewModel.selectedCart.take(1).toList().singleOrNull()

        result shouldBe selectedCart

    }

    @Test
    fun setSelectedCartX100(): Unit = runBlocking {
        (0..100).forEach { i ->
            val newCart = Cart(cartName = "cart$i").apply {
                selected = i % 3 == 0
            }
            viewModel.add(newCart)
            delay(10)
            if (i % 7 == 0) {
                viewModel.selectCart(newCart)
            }

        }
        val selectedCartList = cartDao.cartTable.filter { it.selected }
        selectedCartList shouldHaveSize 1
        viewModel.selectedCart.value shouldBe selectedCartList[0]
        viewModel.selectedCart.take(1).toList()[0] shouldBe selectedCartList[0]
        selectedCartList[0].cartName shouldBe "cart99"
    }

    @Test
    fun setSelectedCart(): Unit = runBlocking {
        val initialySelected = Cart().apply { selected = true; cartName = "initialySelected" }
        val eventuallySelected = Cart().apply { selected = false; cartName = "eventuallySelected" }
        Log.d(tag, "0st \t" + cartDao.cartTable.joinToString())
        viewModel.add(initialySelected)
        delay(10)
        Log.d(tag, "1st \t" + cartDao.cartTable.joinToString())
        viewModel.add(eventuallySelected)
        delay(10)
        Log.d(tag, "2st \t" + cartDao.cartTable.joinToString())
        viewModel.selectedCart.take(1).toList().singleOrNull() shouldBe initialySelected
        viewModel.selectCart(eventuallySelected)
        delay(10)
        Log.d(tag, "3st \t" + cartDao.cartTable.joinToString())
        val expectedToBeSelected = eventuallySelected.copy(selected = true)
        cartDao.getSelectedCart() shouldBe expectedToBeSelected
        viewModel.selectedCart.take(1).toList().singleOrNull() shouldBe expectedToBeSelected
    }

    @Test
    fun getCartItemByItemId(): Unit = runBlocking {
        val cart1 = Cart("cart1").apply { selected = false }
        val cart2 = Cart("cart2").apply { selected = false }
        val item1 = Item()
        val cart1Item1 = CartItem(item1, cart1.cartId)
        val cart2Item1 = CartItem(item1, cart2.cartId)
        viewModel.add(cart1)
        viewModel.add(cart2)
        Thread.sleep(100)
        cartDao.cartTable shouldHaveSize 3
        viewModel.add(item1)
        viewModel.add(cart1Item1)
        viewModel.add(cart2Item1)
        Thread.sleep(100)
        cartDao.cartItemPropertiesTable shouldHaveSize 2

        viewModel.selectCart(cart1)
        Thread.sleep(100)
        cartDao.getSelectedCart() shouldBe cart1
        viewModel.selectedCart.value shouldBe cart1
        viewModel.getCartItemPropertiesByItemId(cart1Item1.item.itemId) shouldBe cart1Item1.cartItemProperties

        viewModel.selectCart(cart2)
        Thread.sleep(100)
        viewModel.getCartItemPropertiesByItemId(cart2Item1.item.itemId) shouldBe cart2Item1.cartItemProperties
    }

}
