package de.moyapro.nushppinglist.sync

import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.db.model.Cart
import de.moyapro.nushppinglist.mock.CartDaoMock
import de.moyapro.nushppinglist.sync.handler.RequestCartMessageHandler
import de.moyapro.nushppinglist.sync.messages.RequestCartMessage
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.util.createSampleCartItem
import de.moyapro.nushppinglist.ui.util.waitFor
import de.moyapro.nushppinglist.util.MainCoroutineRule
import de.moyapro.nushppinglist.util.test.MockPublisher
import io.kotest.matchers.string.shouldContain
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
class RequestCartMessageHandlerTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var viewModel: CartViewModel
    private lateinit var publisher: MockPublisher
    private val cartDao: CartDaoMock =
        CartDaoMock(CoroutineScope(TestCoroutineDispatcher() + SupervisorJob()))

    @Before
    fun setup() {
        viewModel = CartViewModel(cartDao)
        publisher = MockPublisher(CONSTANTS.MQTT_TOPIC_CART)
    }

    @After
    fun tearDown() {
        cartDao.reset()
        publisher.reset()
    }

    @Test(timeout = 10_000)
    fun handleCartRequest__success() = runBlocking {
        val cart = Cart()
        val cartItem1 = createSampleCartItem().apply { cartItemProperties.inCart = cart.cartId }
        val cartItem2 = createSampleCartItem().apply { cartItemProperties.inCart = cart.cartId }
        viewModel.add(cart)
        viewModel.add(cartItem1)
        viewModel.add(cartItem2)
        val request = RequestCartMessage(cart.cartId)
        val requestHandler = RequestCartMessageHandler(cartDao, publisher)
        requestHandler(request)
        Thread.sleep(100) // wait for DB to save
        waitFor { publisher.messages.isNotEmpty() }
        publisher.messages[CONSTANTS.MQTT_TOPIC_CART] shouldContain cartItem1.item.itemId.id.toString()
        publisher.messages[CONSTANTS.MQTT_TOPIC_CART] shouldContain cartItem2.item.itemId.id.toString()
        Unit
    }

}

