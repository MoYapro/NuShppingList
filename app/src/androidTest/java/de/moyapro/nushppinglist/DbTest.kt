package de.moyapro.nushppinglist

import android.content.Context
import androidx.room.Room.inMemoryDatabaseBuilder
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DbTest {
    private lateinit var cartDao: CartDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        cartDao = db.cartDao()
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
        val dbItem = cartDao.findAllItems().take(1).toList()[0].single()
        assertEquals(item, dbItem)
    }

    @Test(timeout = 10000)
    @Throws(Exception::class)
    fun updateAndLoadItem() = runBlocking {
        val newName = "newName"
        val item = Item("Milk")
        cartDao.save(item)
        cartDao.updateAll(item.copy(name = newName))
        val dbItem = cartDao.findAllItems().take(1).toList()[0].single()
        assertEquals(newName, dbItem.name)
    }

    @Test(timeout = 10000)
    @Throws(Exception::class)
    fun writeAndLoadCartItemProperties() = runBlocking {
        val cartItemProperties = CartItemProperties()
        cartDao.save(cartItemProperties)
        val dbCartItemProperties = cartDao.findAllInCart().take(1).toList()[0].single()
        assertEquals(cartItemProperties, dbCartItemProperties)
    }

    @Test(timeout = 10000)
    @Throws(Exception::class)
    fun updateAndLoadCartItemProperties() = runBlocking {
        val cartItemProperties = CartItemProperties()
        val newAmount = 3
        cartDao.save(cartItemProperties)
        cartDao.updateAll(cartItemProperties.copy(amount = newAmount))
        val dbCartItemProperties = cartDao.findAllInCart().take(1).toList()[0].single()
        assertEquals(newAmount, dbCartItemProperties.amount)
    }

    @Test(timeout = 10000)
    @Throws(Exception::class)
    fun writeAndLoadCartItem() = runBlocking {
        val cartItem = CartItem("Milk")
        cartDao.save(cartItem.item)
        cartDao.save(cartItem.cartItemProperties)
        val dbCartItemProperties = cartDao.findAllCartItems().take(1).toList()[0].single()
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
        val dbCartItemProperties = cartDao.findAllCartItems().take(1).toList()[0].single()
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
        val dbCartItemProperties = cartDao.findAllCartItems().take(1).toList()[0].single()
        assertEquals(newAmount, dbCartItemProperties.cartItemProperties.amount)
    }

}
