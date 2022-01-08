package de.moyapro.nushppinglist.sync

import de.moyapro.nushppinglist.mock.CartDaoMock
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.util.createSampleCartItem
import de.moyapro.nushppinglist.ui.util.waitFor
import de.moyapro.nushppinglist.util.MainCoroutineRule
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.StandardTestDispatcher
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SyncTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var syncServiceAlice: SyncService
    private lateinit var viewModelAlice: CartViewModel

    private lateinit var syncServiceBob: SyncService
    private lateinit var viewModelBob: CartViewModel

    @Before
    fun setup() {
        val (viewModelAlice, syncServiceAlice) = setupSyncService("alice")
        this.syncServiceAlice = syncServiceAlice
        this.viewModelAlice = viewModelAlice
        val (viewModelBob, syncServiceBob) = setupSyncService("bob")
        this.syncServiceBob = syncServiceBob
        this.viewModelBob = viewModelBob
    }

    private fun setupSyncService(clientName: String): Pair<CartViewModel, SyncService> {
        val viewModel =
            CartViewModel(
                CartDaoMock(CoroutineScope(StandardTestDispatcher() + SupervisorJob()))
            )
        val serviceAdapter =
            MqttServiceAdapter(clientName).connect()

        return Pair(viewModel, SyncService(serviceAdapter, viewModel))
    }


    companion object {

        fun buildMqttOptions(): MqttConnectOptions {
            val options = MqttConnectOptions()
            options.serverURIs = arrayOf("tcp://192.168.1.101:31883")
            options.userName = "homeassistant"
            options.password = "password".toCharArray()
            options.isCleanSession = false
            return options
        }
    }

    @Test(timeout = 10_000)
    fun syncItem() {
        val cartItem = createSampleCartItem()
        val item = cartItem.item
        viewModelBob.add(cartItem)
        syncServiceAlice.requestItem(item.itemId)
        waitFor { viewModelAlice.getItemByItemId(item.itemId) != null }
        val resultItem = viewModelAlice.getItemByItemId(item.itemId)
        resultItem?.itemId shouldBe item.itemId
        resultItem?.name shouldBe item.name
    }

    @Test(timeout = 100_000)
    fun syncCart() {
        val cartItem = createSampleCartItem()
        viewModelBob.add(cartItem)
        Thread.sleep(500)
        syncServiceAlice.requestCart()
        Thread.sleep(500000)
        viewModelBob.getItemByItemId(cartItem.item.itemId) shouldNotBe null
        viewModelAlice.getCartItemPropertiesByItemId(cartItem.item.itemId) shouldBe cartItem.cartItemProperties
    }
}
