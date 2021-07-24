package de.moyapro.nushppinglist

import android.content.Context
import androidx.room.Room.inMemoryDatabaseBuilder
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

//@RunWith(AndroidJUnit4::class)
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

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() {
        val item = Item("Milk")
        cartDao.save(item)
        val byName = cartDao.findAllInCart()
//        assertEquals(byName, item)
    }
}