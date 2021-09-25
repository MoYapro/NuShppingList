package de.moyapro.nushppinglist

import android.content.Context
import androidx.room.Room.inMemoryDatabaseBuilder
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import de.moyapro.nushppinglist.db.AppDatabase
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.Item
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import kotlin.time.ExperimentalTime

@RunWith(AndroidJUnit4::class)
class DbTest {
    private lateinit var cartDao: CartDao
    private lateinit var viewModel: VM
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        cartDao = db.cartDao()
        viewModel = VM(cartDao)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test(timeout = 10000)
    @Throws(Exception::class)
    fun writeAndLoadItem() = runBlocking {
        val item = Item("Milk")
        cartDao.save(item)
        val dbItem = cartDao.findAllItems().first().first()
        item shouldBe dbItem
    }

    @Test(timeout = 10000)
    @Throws(Exception::class)
    fun updateAndLoadItem() = runBlocking {
        val newName = "newName"
        val item = Item("Milk")
        cartDao.save(item)
        cartDao.updateAll(item.copy(name = newName))
        val dbItem = cartDao.findAllItems().first().first()
        assertEquals(newName, dbItem.name)
    }

    @Test(timeout = 10000)
    @Throws(Exception::class)
    fun writeAndLoadCartItemProperties() = runBlocking {
        val cartItem = CartItem("someName")
        cartDao.save(cartItem.item)
        cartDao.save(cartItem.cartItemProperties)
        val dbCartItemProperties = cartDao.findAllInCart().first().first()
        assertEquals(cartItem.cartItemProperties, dbCartItemProperties)
    }

    @Test(timeout = 10000)
    @Throws(Exception::class)
    fun updateAndLoadCartItemProperties() = runBlocking {
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
    fun writeAndLoadCartItem() = runBlocking {
        val cartItem = CartItem("Milk")
        cartDao.save(cartItem.item)
        cartDao.save(cartItem.cartItemProperties)
        val dbCartItemProperties = cartDao.findAllCartItems().first().first()
        assertEquals(cartItem, dbCartItemProperties)
    }

    @Test(timeout = 10000)
    @Throws(Exception::class)
    fun updateAndLoadCartItem_item() = runBlocking {
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
    fun updateAndLoadCartItem_cartItemProperties() = runBlocking {
        val newAmount = 4
        val cartItem = CartItem("Milk")
        cartDao.save(cartItem.item)
        cartDao.save(cartItem.cartItemProperties)
        cartDao.updateAll(cartItem.cartItemProperties.copy(amount = newAmount))
        val dbCartItemProperties = cartDao.findAllCartItems().first().first()
        assertEquals(newAmount, dbCartItemProperties.cartItemProperties.amount)
    }

    @OptIn(ExperimentalTime::class)
    @Test(timeout = 10000)
    @Throws(Exception::class)
    fun subscribeToCartItemUpdates() = runBlocking {
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

    }

    @OptIn(ExperimentalTime::class)
    @Test(timeout = 10000)
    fun addExistingItemByName() = runBlockingTest {
        val itemName = "Milk"
        val newItem = Item(itemName)
        viewModel.add(newItem)
        viewModel.addToCart(itemName)

        viewModel.allCartItems.test {
            val cartItem = awaitItem()
            cartItem.single().item.name shouldBe itemName
        }
    }

}
