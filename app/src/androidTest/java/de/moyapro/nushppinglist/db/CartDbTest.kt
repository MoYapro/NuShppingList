package de.moyapro.nushppinglist.db

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.model.Cart
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.util.DbTestHelper
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
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
        val cartItem = CartItem("someName")
        cartDao.save(cartItem.item)
        cartDao.save(cartItem.cartItemProperties)
        val dbCartItemProperties = cartDao.findAllInCart().first().first()
        assertEquals(cartItem.cartItemProperties, dbCartItemProperties)
    }

    @Test(timeout = 10000)
    @Throws(Exception::class)
    fun updateAndLoadCartItemProperties(): Unit = runBlocking {
        val cartItem = CartItem("someName")
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
        val cartItem = CartItem("Milk")
        cartDao.save(cartItem.item)
        cartDao.save(cartItem.cartItemProperties)
        val dbCartItemProperties = cartDao.findAllCartItems().first().first()
        assertEquals(cartItem, dbCartItemProperties)
    }

    @Test(timeout = 10000)
    @Throws(Exception::class)
    fun updateAndLoadCartItem_item(): Unit = runBlocking {
        val newName = "NoMilk"
        val cartItem = CartItem("Milk")
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
        val cartItem = CartItem("Milk")
        cartDao.save(cartItem.item)
        cartDao.save(cartItem.cartItemProperties)
        cartDao.updateAll(cartItem.cartItemProperties.copy(amount = newAmount))
        val dbCartItemProperties = cartDao.findAllCartItems().first().first()
        assertEquals(newAmount, dbCartItemProperties.cartItemProperties.amount)
        Unit
    }

    @OptIn(ExperimentalTime::class)
    @Test(timeout = 10000)
    @Throws(Exception::class)
    fun subscribeToCartItemUpdates(): Unit = runBlocking {
        val newAmount = 4
        var currentCartItems = 0
        var currentAmount: Int? = 0
        val cartItem = CartItem("Milk")

        cartDao.save(cartItem.item)
        cartDao.save(cartItem.cartItemProperties)
        cartDao.updateAll(cartItem.cartItemProperties.copy(amount = newAmount))

        cartDao.findAllCartItems().test {
            val cartItemFromDb = awaitItem()
            cartItemFromDb.single().cartItemProperties.amount shouldBe newAmount
            cartItemFromDb.single().item.name shouldBe cartItem.item.name
        }

        Unit
    }

    @OptIn(ExperimentalTime::class)
    @Test(timeout = 10000)
    fun addExistingItemByName(): Unit = runBlocking {
        val itemName = "Milk"
        val newItem = Item(itemName)
        viewModel.add(newItem)
        viewModel.addToCart(itemName)
        delay(1.seconds)
        cartDao.findAllCartItems().test {
            val cartItemFromDb = awaitItem()
            cartItemFromDb.single().cartItemProperties.amount shouldBe 1
            cartItemFromDb.single().item.name shouldBe newItem.name
        }

        Unit
    }

    @Test
    fun getSelectedCart(): Unit = runBlocking {
        val selectedCart = Cart(cartName = "Selected").apply { selected = true }
        val notSelectedCart = Cart(cartName = "Not selected").apply { selected = false }
        viewModel.add(selectedCart)
        viewModel.add(notSelectedCart)
        delay(1.seconds)
        val result = viewModel.getSelectedCart()

        result shouldBe selectedCart

        Unit
    }

    @Test
    fun setSelectedCart(): Unit = runBlocking {
        val initialySelected = Cart().apply { selected = true }
        val eventuallySelected = Cart().apply { selected = false }
        viewModel.add(initialySelected)
        viewModel.add(eventuallySelected)
        delay(1.seconds)

        viewModel.selectCart(eventuallySelected)
        delay(1.seconds)

        val carts: List<Cart> = viewModel.allCart.take(1).first()

        carts.single { it == initialySelected }.selected shouldBe false
        carts.single { it == eventuallySelected }.selected shouldBe true

        Unit
    }

    @Test
    fun getItemsInSpecificCart(): Unit = runBlocking {
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
        delay(1.seconds)
        // no cart selected
        val emptyCart = viewModel.allCartItemsGrouped.take(1).first()
        emptyCart shouldBe emptyMap()

        viewModel.selectCart(cart1)
        delay(1.seconds)
        val cart1Items = viewModel.allCartItemsGrouped.take(1).first().values.flatten()
        cart1Items shouldContainExactlyInAnyOrder listOf(cart1Item1, cart1Item2)

        viewModel.selectCart(cart2)
        delay(1.seconds)
        val cart2Items = viewModel.allCartItemsGrouped.take(1).first().values.flatten()
        cart2Items shouldContainExactlyInAnyOrder listOf(cart2Item1, cart2Item2)

        Unit
    }

    @Test
    @ExperimentalTime
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
        delay(100.milliseconds)
        viewModel.getCartItemPropertiesByItemId(cart1Item1.item.itemId) shouldBe cart1Item1.cartItemProperties

        viewModel.selectCart(cart2)
        delay(100.milliseconds)
        viewModel.getCartItemPropertiesByItemId(cart2Item1.item.itemId) shouldBe cart2Item1.cartItemProperties

        Unit
    }
}
