package de.moyapro.nushppinglist.sync

import de.moyapro.nushppinglist.MockPublisher
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.mock.CartDaoMock
import de.moyapro.nushppinglist.sync.handler.CartItemUpdateMessageHandler
import de.moyapro.nushppinglist.sync.messages.CartItemUpdateMessage
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.util.createSampleCartItem
import de.moyapro.nushppinglist.util.MainCoroutineRule
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@Suppress("EXPERIMENTAL_API_USAGE")
@ExperimentalCoroutinesApi
class CartItemUpdateMessageHandlerTest {

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

    @Test(timeout = 100_000)
    fun handleRequest__success() = runBlocking {
        val cartItem = createSampleCartItem()
        viewModel.add(cartItem)
        val updatedCartItemProperties = cartItem.cartItemProperties.copy(
            amount = cartItem.cartItemProperties.amount + 1,
            checked = !cartItem.cartItemProperties.checked
        )
        val request = CartItemUpdateMessage(updatedCartItemProperties)
        CartItemUpdateMessageHandler(cartDao, publisher)(request)
        Thread.sleep(1000) // wait for DB to save
        val result = viewModel.getCartItemPropertiesByItemId(cartItem.item.itemId)
        result?.amount shouldBe updatedCartItemProperties.amount
        result?.checked shouldBe updatedCartItemProperties.checked
    }
}

