package de.moyapro.nushppinglist.sync

import de.moyapro.nushppinglist.db.model.Cart
import de.moyapro.nushppinglist.mock.CartDaoMock
import de.moyapro.nushppinglist.sync.handler.RequestCartMessageHandler
import de.moyapro.nushppinglist.sync.messages.CartMessage
import de.moyapro.nushppinglist.sync.messages.RequestCartMessage
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.util.createSampleCartItem
import de.moyapro.nushppinglist.util.MainCoroutineRule
import de.moyapro.nushppinglist.util.test.MockPublisher
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
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
    private val cartDao: CartDaoMock =
        CartDaoMock(CoroutineScope(TestCoroutineDispatcher() + SupervisorJob()))

    @Before
    fun setup() {
        viewModel = CartViewModel(cartDao)
    }

    @After
    fun tearDown() {
        cartDao.reset()
        MockPublisher.reset()
    }

    @Test(timeout = Long.MAX_VALUE)
    fun handleCartRequest__success(): Unit = runBlocking {
        val cart = Cart()
        val cartItem1 = createSampleCartItem().apply { cartItemProperties.inCart = cart.cartId }
        val cartItem2 = createSampleCartItem().apply { cartItemProperties.inCart = cart.cartId }
        viewModel.add(cart)
        viewModel.add(cartItem1)
        viewModel.add(cartItem2)
        Thread.sleep(100)
        val requestHandler = RequestCartMessageHandler(cartDao, MockPublisher)
        requestHandler(RequestCartMessage(cart.cartId))
        Thread.sleep(100)
        with((MockPublisher.messages.values.single().single() as CartMessage)) {
            this.cartItemPropertiesList shouldContainExactlyInAnyOrder listOf(cartItem1, cartItem2).map { it.cartItemProperties }
        }
    }

}

