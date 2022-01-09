package de.moyapro.nushppinglist.sync

import de.moyapro.nushppinglist.mock.CartDaoMock
import de.moyapro.nushppinglist.sync.messages.RequestCartMessage
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.util.createSampleCartItem
import de.moyapro.nushppinglist.ui.util.waitFor
import de.moyapro.nushppinglist.util.MainCoroutineRule
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.After
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
    @After
    fun teardown() {
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
    fun syncCartWithExistingItems() {
        val cartItem = createSampleCartItem()
        val item = cartItem.item
        viewModelBob.add(item)
        viewModelAlice.add(cartItem)
        viewModelBob.getCartItemPropertiesByItemId(item.itemId) shouldBe null
        syncServiceBob.publish(RequestCartMessage("RequestCartMessage"))
        waitFor { null != viewModelBob.getCartItemPropertiesByItemId(item.itemId)}
        val resultItem = viewModelBob.getCartItemPropertiesByItemId(item.itemId)
        resultItem?.itemId shouldBe item.itemId
    }

    @Test(timeout = 3_000)
    fun syncCartWithoutExistingItems() {
        val cartItem = createSampleCartItem()
        val item = cartItem.item
        viewModelAlice.add(cartItem)
        viewModelBob.getCartItemPropertiesByItemId(item.itemId) shouldBe null
        syncServiceBob.publish(RequestCartMessage("RequestCartMessage"))
        waitFor { null != viewModelBob.getCartItemPropertiesByItemId(item.itemId)}
        val resultItem = viewModelBob.getCartItemPropertiesByItemId(item.itemId)
        resultItem?.itemId shouldBe item.itemId
    }
}
