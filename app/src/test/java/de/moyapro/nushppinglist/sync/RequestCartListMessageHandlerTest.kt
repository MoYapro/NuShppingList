package de.moyapro.nushppinglist.sync

import de.moyapro.nushppinglist.db.model.Cart
import de.moyapro.nushppinglist.mock.CartDaoMock
import de.moyapro.nushppinglist.sync.handler.RequestCartListMessageHandler
import de.moyapro.nushppinglist.sync.messages.CartListMessage
import de.moyapro.nushppinglist.sync.messages.RequestCartListMessage
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.util.MainCoroutineRule
import de.moyapro.nushppinglist.util.test.MockPublisher
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
class RequestCartListMessageHandlerTest {

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

    @Test(timeout = 10_000)
    fun handleCartListRequest__success(): Unit = runBlocking {
        val cart = Cart(cartName = "cart1", synced = true)
        viewModel.add(cart)
        val request = RequestCartListMessage()
        val requestHandler = RequestCartListMessageHandler(cartDao, MockPublisher)
        requestHandler(request)
        Thread.sleep(100)
        with((MockPublisher.messages.values.single().single() as CartListMessage).carts.single()) {
            this.cartId shouldBe cart.cartId
            this.cartName shouldBe cart.cartName
        }
    }

}

