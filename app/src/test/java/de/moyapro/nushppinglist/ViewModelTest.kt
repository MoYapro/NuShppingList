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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.*
import org.junit.Assert.*
import java.util.*
import kotlin.random.Random


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
    fun removeCheckedItemFromCart(): Unit = runBlocking {
        val itemToRemove = Item("remove me")
        viewModel.add(CartItem(itemToRemove).apply { cartItemProperties.checked = true })
        Thread.sleep(100)
        cartDao.cartItemPropertiesTable.single().checked shouldBe true
        viewModel.removeCheckedFromCart()
        Thread.sleep(100)
        viewModel.getItemByItemId(itemToRemove.itemId) shouldBe itemToRemove
        viewModel.getCartItemPropertiesByItemId(itemToRemove.itemId) shouldBe null
        cartDao.cartItemPropertiesTable shouldBe emptyList()
    }

    @Test
    fun setChecked(): Unit = runBlocking {
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

        viewModel.toggleChecked(item1.cartItemProperties)
        viewModel.toggleChecked(item2.cartItemProperties)
        Thread.sleep(100)
        assertTrue("All are unchecked after toggeling again",
            cartItems.all { !it.checked })
    }

    @Test
    fun setCheckedIsPersisted(): Unit = runBlocking {
        val item = CartItem("my item")
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
    fun addNewItemByName(): Unit = runBlocking {
        val itemName = "Milk"
        viewModel.addToCart(itemName)
        Thread.sleep(100)
        with(cartDao.itemTable.single()) {
            this.name shouldBe itemName
        }
    }

    @Test
    fun addExistingItemByName(): Unit = runBlocking {
        val itemName = "Milk"
        val newItem = Item(itemName)
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
        viewModel.add(newItem)
        repeat(10) { i ->
            viewModel.addToCart(newItem)
            Thread.sleep(100)
            with(viewModel.cartItems.take(1).toList().flatten().single()) {
                this.itemId shouldBe newItem.itemId
                this.amount shouldBe (1 + i)
            }
            with(viewModel.allCartItems.take(1).toList().flatten().single()) {
                this.cartItemProperties.itemId shouldBe newItem.itemId
                this.cartItemProperties.amount shouldBe (1 + i)
            }
        }
    }

    @Test
    fun addExistingItemAsItemMultiple(): Unit = runBlocking {
        val repetitions = 99
        val newItem = Item("Itemname${Random.nextLong()}")
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
    fun getCartItemPropertiesForItem(): Unit = runBlocking {
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
    fun updateCartItemProperties(): Unit = runBlocking {
        val itemId = ItemId()
        val originalCartItemProperties =
            CartItemProperties(newItemId = itemId).apply { checked = true }
        val cartItem = CartItem(originalCartItemProperties, Item("x", itemId))
        val updatedCartItemProperties = originalCartItemProperties.copy(checked = false)
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
        carts.single() shouldBe cart
    }

    @Test
    fun updateCart(): Unit = runBlocking {
        val cart = Cart()
        viewModel.add(cart)
        Thread.sleep(100)
        val carts = viewModel.allCart.take(1).toList().flatten()
        carts.single() shouldBe cart

        val updatedCart = cart.copy(cartName = "newName", synced = !cart.synced, selected = true)
        viewModel.update(updatedCart)
        Thread.sleep(100)
        cartDao.cartTable.single() shouldBe updatedCart
    }

    @Test
    fun deleteCart(): Unit = runBlocking {
        val cart = Cart()
        viewModel.add(cart)
        Thread.sleep(100)
        val carts = viewModel.allCart.take(1).toList().flatten()
        carts.single() shouldBe cart

        viewModel.removeCart(cart)
    }


    @Test
    fun getSelectedCart(): Unit = runBlocking {
        val selectedCart = Cart(cartName = "Selected").apply { selected = true }
        val notSelectedCart = Cart(cartName = "Not selected").apply { selected = false }
        viewModel.add(selectedCart)
        viewModel.add(notSelectedCart)
        Thread.sleep(100)
        val result = viewModel.getSelectedCart()

        result shouldBe selectedCart

    }

    @Test
    fun setSelectedCart(): Unit = runBlocking {
        val initialySelected = Cart().apply { selected = true; cartName = "initialySelected" }
        val eventuallySelected = Cart().apply { selected = false; cartName = "eventuallySelected" }
        viewModel.add(initialySelected)
        Thread.sleep(100)
        viewModel.add(eventuallySelected)
        Thread.sleep(100)
        viewModel.getSelectedCart() shouldBe initialySelected
        viewModel.selectCart(eventuallySelected)
        Thread.sleep(100)

        val carts: List<Cart> = cartDao.cartTable.toList()
        cartDao.cartTable shouldHaveSize 2
        carts.single { it.cartId == eventuallySelected.cartId }.selected shouldBe true
        carts.single { it.cartId == initialySelected.cartId }.selected shouldBe false
    }

    @Test
    fun getItemsInSpecificCart(): Unit = runBlocking {
        val cart1 = Cart().apply { selected = false }
        val cart2 = Cart().apply { selected = false }
        val cart1Item1 = CartItem(Item()).apply {
            cartItemProperties.inCart = cart1.cartId; item.name = "cart1Item1"
        }
        val cart1Item2 = CartItem(Item()).apply {
            cartItemProperties.inCart = cart1.cartId; item.name = "cart1Item2"
        }
        val cart2Item1 = CartItem(Item()).apply {
            cartItemProperties.inCart = cart2.cartId; item.name = "cart2Item1"
        }
        val cart2Item2 = CartItem(Item()).apply {
            cartItemProperties.inCart = cart2.cartId; item.name = "cart2Item2"
        }
        viewModel.add(cart1)
        viewModel.add(cart2)
        viewModel.add(cart1Item1)
        viewModel.add(cart1Item2)
        viewModel.add(cart2Item1)
        viewModel.add(cart2Item2)
        Thread.sleep(100)
        // no cart selected
        val emptyCart = viewModel.allCartItemsGrouped.take(1).first()
        emptyCart shouldBe emptyMap()

        viewModel.selectCart(cart1)
        Thread.sleep(100)
        val cart1Items = viewModel.allCartItemsGrouped.take(1).first().values.flatten()
        cart1Items shouldContainExactlyInAnyOrder listOf(cart1Item1, cart1Item2)

        viewModel.selectCart(cart2)
        Thread.sleep(100)
        val cart2Items = viewModel.allCartItemsGrouped.take(1).first().values.flatten()
        cart2Items shouldContainExactlyInAnyOrder listOf(cart2Item1, cart2Item2)

    }

    @Test
    fun getCartItemByItemId(): Unit = runBlocking {
        val cart1 = Cart().apply { selected = false }
        val cart2 = Cart().apply { selected = false }
        val item1 = Item()
        val cart1Item1 = CartItem(item1).apply { cartItemProperties.inCart = cart1.cartId }
        val cart2Item1 = CartItem(item1).apply { cartItemProperties.inCart = cart2.cartId }
        viewModel.add(cart1)
        viewModel.add(cart2)
        viewModel.add(cart1Item1)
        viewModel.add(cart2Item1)

        viewModel.selectCart(cart1)
        Thread.sleep(100)
        viewModel.getCartItemPropertiesByItemId(cart1Item1.item.itemId) shouldBe cart1Item1.cartItemProperties

        viewModel.selectCart(cart2)
        Thread.sleep(100)
        viewModel.getCartItemPropertiesByItemId(cart2Item1.item.itemId) shouldBe cart2Item1.cartItemProperties
    }

}
