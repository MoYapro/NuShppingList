package de.moyapro.nushppinglist.db

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import de.moyapro.nushppinglist.constants.CONSTANTS.DEFAULT_CART
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.model.Cart
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.util.DbTestHelper
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import kotlin.time.ExperimentalTime

@RunWith(AndroidJUnit4::class)
@ExperimentalTime
class CartDbTest {
    private lateinit var cartDao: CartDao
    private lateinit var viewModel: CartViewModel
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        db = DbTestHelper.createTestDatabase()
        cartDao = db.cartDao()
        viewModel = CartViewModel(cartDao)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test(timeout = 10000)
    @Throws(Exception::class)
    fun writeAndLoadItem(): Unit = runBlocking {
        val item = Item("Milk")
        cartDao.save(item)
        Thread.sleep(100)
        val dbItem = cartDao.findAllItems().first().first()
        item shouldBe dbItem
    }

    @Test(timeout = 10000)
    @Throws(Exception::class)
    fun updateAndLoadItem(): Unit = runBlocking {
        val newName = "newName"
        val item = Item("Milk")
        cartDao.save(item)
        cartDao.updateAll(item.copy(name = newName))
        val dbItem = cartDao.findAllItems().first().first()
        assertEquals(newName, dbItem.name)
    }

    @Test(timeout = 10000)
    @Throws(Exception::class)
    fun writeAndLoadCartItemProperties(): Unit = runBlocking {
        val cartItem = CartItem("someName", DEFAULT_CART.cartId)
        cartDao.save(cartItem.item)
        cartDao.save(cartItem.cartItemProperties)
        val dbCartItemProperties = cartDao.findAllInCart().first().first()
        assertEquals(cartItem.cartItemProperties, dbCartItemProperties)
    }

    @Test(timeout = 10000)
    @Throws(Exception::class)
    fun updateAndLoadCartItemProperties(): Unit = runBlocking {
        val cartItem = CartItem("someName", DEFAULT_CART.cartId)
        val newAmount = 3
        cartDao.save(cartItem.item)
        cartDao.save(cartItem.cartItemProperties)
        cartDao.updateAll(cartItem.cartItemProperties.copy(amount = newAmount))
        val dbCartItemProperties = cartDao.findAllInCart().first().first()
        assertEquals(newAmount, dbCartItemProperties.amount)
    }

    @Test(timeout = 10000)
    @Throws(Exception::class)
    fun writeAndLoadCartItem(): Unit = runBlocking {
        val cartItem = CartItem("Milk", DEFAULT_CART.cartId)
        cartDao.save(cartItem.item)
        cartDao.save(cartItem.cartItemProperties)
        val dbCartItemProperties = cartDao.findAllCartItems().first().first()
        assertEquals(cartItem, dbCartItemProperties)
    }

    @Test(timeout = 10000)
    @Throws(Exception::class)
    fun updateAndLoadCartItem_item(): Unit = runBlocking {
        val newName = "NoMilk"
        val cartItem = CartItem("Milk", DEFAULT_CART.cartId)
        cartDao.save(cartItem.item)
        cartDao.save(cartItem.cartItemProperties)
        cartDao.updateAll(cartItem.item.copy(name = newName))
        val dbCartItemProperties = cartDao.findAllCartItems().first().first()
        assertEquals(newName, dbCartItemProperties.item.name)
    }

    @Test(timeout = 10000)
    @Throws(Exception::class)
    fun updateAndLoadCartItem_cartItemProperties(): Unit = runBlocking {
        val newAmount = 4
        val cartItem = CartItem("Milk", DEFAULT_CART.cartId)
        cartDao.save(cartItem.item)
        cartDao.save(cartItem.cartItemProperties)
        cartDao.updateAll(cartItem.cartItemProperties.copy(amount = newAmount))
        Thread.sleep(100)
        val dbCartItemProperties = cartDao.findAllCartItems().first().first()
        assertEquals(newAmount, dbCartItemProperties.cartItemProperties.amount)
    }

    @OptIn(ExperimentalTime::class)
    @Test(timeout = 10000)
    @Throws(Exception::class)
    fun subscribeToCartItemUpdates(): Unit = runBlocking {
        val newAmount = 4
        var currentCartItems = 0
        var currentAmount: Int? = 0
        val cartItem = CartItem("Milk", DEFAULT_CART.cartId)

        cartDao.save(cartItem.item)
        cartDao.save(cartItem.cartItemProperties)
        cartDao.updateAll(cartItem.cartItemProperties.copy(amount = newAmount))

        cartDao.findAllCartItems().test {
            val cartItemFromDb = awaitItem()
            cartItemFromDb.single().cartItemProperties.amount shouldBe newAmount
            cartItemFromDb.single().item.name shouldBe cartItem.item.name
        }
    }

    @Test
    fun saveNewItem(): Unit = runBlocking {
        repeat(10) { i ->
            viewModel.add(Item("item$i"))
            Thread.sleep(100)
            val items = cartDao.findAllItems().take(1).toList().flatten()
            items shouldHaveSize (i + 1)
        }
    }

    @OptIn(ExperimentalTime::class)
    @Test(timeout = 10000)
    fun addExistingItemToCartByName(): Unit = runBlocking {
        val itemName = "Milk"
        val newItem = Item(itemName)
        viewModel.add(newItem)
        Thread.sleep(100)
        viewModel.addToCart(itemName)
        Thread.sleep(100)
        val cartItemFromDb = cartDao.findAllCartItems().take(1).first().single()
        cartItemFromDb.cartItemProperties.amount shouldBe 1
        cartItemFromDb.item.name shouldBe newItem.name
    }

    @Test
    fun getSelectedCart(): Unit = runBlocking {
        val selectedCart = Cart(cartName = "Selected").apply { selected = true }
        val notSelectedCart = Cart(cartName = "Not selected").apply { selected = false }
        viewModel.add(selectedCart)
        viewModel.add(notSelectedCart)
        Thread.sleep(100)
        val result = viewModel.selectedCart.take(1).toList().singleOrNull()

        result shouldBe selectedCart
    }

    @Test
    fun saveLoadCart(): Unit = runBlocking {
        viewModel.add(DEFAULT_CART)
        repeat(10) { i ->
            viewModel.add(Cart(cartName = "cart$i"))
            Thread.sleep(100)
            viewModel.allCart.take(1).toList()
                .flatten() shouldHaveSize (i + 2) // 2 because i is 0-based and there is the default cart
        }
    }

    @Test
    fun setSelectedCart(): Unit = runBlocking {
        val repetitions = 10
        repeat(repetitions) { i ->
            viewModel.add(Cart(cartName = "cart$i"))
        }
        Thread.sleep(100)
        val savedCarts = viewModel.allCart.take(1).toList().flatten()
        savedCarts shouldHaveSize repetitions
        savedCarts.none { it.selected } shouldBe true
        println("Done inserting ===========")

        savedCarts.forEach { savedCart ->
            viewModel.selectCart(savedCart)
            Thread.sleep(100)
            val currentlySelectedCart = viewModel.selectedCart.take(1).toList().singleOrNull()
            currentlySelectedCart?.cartId shouldBe savedCart.cartId
        }

    }

    @Test
    fun getItemsInSpecificCart(): Unit = runBlocking {
        val numberOfCarts = 3
        val numberOfItemsPerCart = 3
        viewModel.add(DEFAULT_CART)
        repeat(numberOfCarts) { cartNumber ->
            val cart = Cart("cart$cartNumber").apply { selected = false; }
            viewModel.add(cart)
            repeat(numberOfItemsPerCart) { itemNumber ->
                viewModel.add(CartItem(Item("cart${cartNumber}item$itemNumber"), cart.cartId))
            }
        }
        Thread.sleep(1000)
        val savedCarts = viewModel.allCart.take(1).toList().flatten()
        savedCarts shouldHaveSize (numberOfCarts + 1) // 1 for the default cart
        val savedItems = viewModel.allCartItems.take(1).toList()
            .flatten() // check all items got created
        savedItems shouldHaveSize (numberOfCarts * numberOfItemsPerCart)


        savedItems.forEach{ item ->
            val relatedCart = savedCarts.single { it.cartId == item.cartItemProperties.inCart }
            item.item.name shouldContain relatedCart.cartName
        }
    }
}
