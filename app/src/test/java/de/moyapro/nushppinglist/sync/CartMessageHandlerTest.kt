package de.moyapro.nushppinglist.sync

import de.moyapro.nushppinglist.MockPublisher
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.mock.CartDaoMock
import de.moyapro.nushppinglist.sync.handler.CartMessageHandler
import de.moyapro.nushppinglist.sync.messages.CartMessage
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.util.createSampleCartItem
import de.moyapro.nushppinglist.util.MainCoroutineRule
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@Suppress("EXPERIMENTAL_API_USAGE")
@ExperimentalCoroutinesApi
class CartMessageHandlerTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var viewModel: CartViewModel
    private lateinit var publisher: MockPublisher
    private val cartDao: CartDaoMock =
        CartDaoMock(CoroutineScope(TestCoroutineDispatcher() + SupervisorJob()))

    @Before
    fun setup() {
        viewModel = CartViewModel(cartDao)
        publisher = MockPublisher(CONSTANTS.MQTT_TOPIC_ITEM)
    }

    @After
    fun tearDown() {
        cartDao.reset()
        publisher.reset()
    }

    @Test(timeout = 10_000)
    fun handleItemRequest__success() {
        val cartItemList = listOf(
            createSampleCartItem(),
            createSampleCartItem()
        )
        cartItemList.map { it.item }.forEach { viewModel.add(it) }
        val request = CartMessage(cartItemList.map { it.cartItemProperties })
        CartMessageHandler(viewModel, publisher)(request)
        cartItemList.map { it.item.itemId }.forEach { itemId ->
            val resultItem = viewModel.getCartItemPropertiesByItemId(itemId)
            resultItem?.itemId shouldBe itemId
        }
    }
}

