package de.moyapro.nushppinglist.sync

import android.content.SharedPreferences
import de.moyapro.nushppinglist.MainActivity
import de.moyapro.nushppinglist.constants.SETTING
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.model.Cart
import de.moyapro.nushppinglist.mock.CartDaoMock
import de.moyapro.nushppinglist.sync.messages.CartMessage
import de.moyapro.nushppinglist.sync.messages.ItemMessage
import de.moyapro.nushppinglist.sync.messages.RequestCartListMessage
import de.moyapro.nushppinglist.sync.messages.RequestCartMessage
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.util.createSampleCartItem
import de.moyapro.nushppinglist.ui.util.createSampleItem
import de.moyapro.nushppinglist.ui.util.waitFor
import de.moyapro.nushppinglist.util.MainCoroutineRule
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SyncTest {
    init {
        // init preferences before init connection
        val preferences = mockk<SharedPreferences>()
        every {
            preferences.getString(
                SETTING.SYNC_MQTT_SERVER_HOSTNAME.name,
                any()
            )
        } returns "192.168.1.101:31883"
        every {
            preferences.getString(
                SETTING.SYNC_MQTT_SERVER_USER.name,
                any()
            )
        } returns "homeassistant"
        every {
            preferences.getString(
                SETTING.SYNC_MQTT_SERVER_PASSWORD.name,
                any()
            )
        } returns "password"
        every {
            preferences.getString(
                SETTING.SYNC_MQTT_SERVER_BASE_TOPIC.name,
                any()
            )
        } returns "nuShoppingList"
        every { preferences.getBoolean(SETTING.SYNC_MQTT_SERVER_TLS.name, any()) } returns false
        every { preferences.getBoolean(SETTING.SYNC_ENABLED.name, any()) } returns true
        MainActivity.preferences = preferences
    }

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var syncServiceAlice: SyncService
    private lateinit var cartDaoAlice: CartDaoMock
    private lateinit var cartDaoBob: CartDaoMock
    private lateinit var viewModelAlice: CartViewModel

    private lateinit var syncServiceBob: SyncService
    private lateinit var viewModelBob: CartViewModel


    @Before
    fun setup() {
        cartDaoAlice = CartDaoMock(CoroutineScope(StandardTestDispatcher() + SupervisorJob()))
        val (viewModelAlice, syncServiceAlice) = setupSyncService("alice", cartDaoAlice)
        this.syncServiceAlice = syncServiceAlice
        this.viewModelAlice = viewModelAlice
        cartDaoBob = CartDaoMock(CoroutineScope(StandardTestDispatcher() + SupervisorJob()))
        val (viewModelBob, syncServiceBob) = setupSyncService("bob", cartDaoBob)
        this.syncServiceBob = syncServiceBob
        this.viewModelBob = viewModelBob
    }

    @After
    fun teardown() {
        (cartDaoAlice as CartDaoMock).reset()
        (cartDaoBob as CartDaoMock).reset()
    }

    private fun setupSyncService(
        clientName: String,
        cartDao: CartDao = CartDaoMock(
            CoroutineScope(StandardTestDispatcher() + SupervisorJob())
        ),
    ): Pair<CartViewModel, SyncService> {
        val viewModel = CartViewModel(cartDao)
        val serviceAdapter =
            MqttServiceAdapter(clientName).connect()

        return Pair(viewModel, SyncService(serviceAdapter, cartDao))
    }

    @Test(timeout = 10_000)
    fun syncItem() {
        val cartItem = createSampleCartItem()
        val item = cartItem.item
        viewModelBob.add(item)
        syncServiceAlice.requestItem(item.itemId)
        waitFor { viewModelAlice.getItemByItemId(item.itemId) != null }
        val resultItem = viewModelAlice.getItemByItemId(item.itemId)
        resultItem?.itemId shouldBe item.itemId
        resultItem?.name shouldBe item.name
    }

    @Test(timeout = 10_000)
    fun syncCart() {
        val cart = Cart()
        viewModelBob.add(cart)
        cartDaoAlice.cartTable shouldHaveSize 0
        syncServiceAlice.publish(RequestCartListMessage())
        Thread.sleep(1000)
        cartDaoAlice.cartTable shouldHaveSize 1
    }

    @Test(timeout = 10_000)
    fun syncCartWithExistingItems() {
        val cart = Cart()
        val cartItem = createSampleCartItem().apply { cartItemProperties.inCart = cart.cartId }
        val item = cartItem.item
        viewModelBob.add(cart)
        viewModelBob.add(item)
        viewModelAlice.add(cartItem)
        viewModelBob.getCartItemPropertiesByItemId(item.itemId) shouldBe null
        syncServiceBob.publish(RequestCartMessage(cart.cartId))
        waitFor { null != viewModelBob.getCartItemPropertiesByItemId(item.itemId) }
        val resultItem = viewModelBob.getCartItemPropertiesByItemId(item.itemId)
        resultItem?.itemId shouldBe item.itemId
    }

    @Test(timeout = Long.MAX_VALUE)
    fun syncCartWithoutExistingItems() {
        val cart = Cart(cartName = "Cart ${Math.random()}").apply { synced = true }
        val cartItem = createSampleCartItem().apply { cartItemProperties.inCart = cart.cartId }
        val item = cartItem.item
        viewModelAlice.add(cart)
        viewModelAlice.add(cartItem)
        viewModelBob.getCartItemPropertiesByItemId(item.itemId) shouldBe null
        syncServiceBob.publish(RequestCartMessage(cart.cartId))
        Thread.sleep(1000)
        cartDaoBob.cartTable shouldContain cart
        cartDaoBob.itemTable shouldContain item
    }

    @Test(timeout = 10_000)
    fun updateCartItem() {
        val cart = Cart()
        val originalCartItem = createSampleCartItem().apply { cartItemProperties.checked = false }
        val itemId = originalCartItem.item.itemId
        val updatedCartItemProperties =
            originalCartItem.cartItemProperties.copy(checked = true)
        viewModelBob.add(originalCartItem)
        syncServiceAlice.publish(CartMessage(listOf(updatedCartItemProperties), cart.cartId))
        waitFor { viewModelBob.getCartItemPropertiesByItemId(itemId)?.checked ?: false }
        val resultItem = viewModelBob.getCartItemPropertiesByItemId(itemId)
        resultItem?.itemId shouldBe itemId
        resultItem?.checked shouldBe true
    }

    @Test(timeout = 10_000)
    fun receiveItemMultipleTimes() {
        val item = createSampleItem()
        repeat(12) {
            syncServiceAlice.publish(ItemMessage(item))
        }
        waitFor { null != viewModelBob.getItemByItemId(item.itemId) }
    }

    @Test(timeout = 10_000)
    fun requestCartList(): Unit = runBlocking {
        val cart = Cart(cartName = "cart${Math.random()}", synced = true)
        viewModelBob.add(cart)
        Thread.sleep(1000)
        (cartDaoBob as CartDaoMock).cartTable shouldHaveSize 1
        (cartDaoAlice as CartDaoMock).cartTable shouldHaveSize 0
        syncServiceAlice.publish(RequestCartListMessage())
        Thread.sleep(1000)
        (cartDaoAlice as CartDaoMock).cartTable shouldContain cart
    }

}
