package de.moyapro.nushppinglist.sync

import de.moyapro.nushppinglist.mock.CartDaoMock
import de.moyapro.nushppinglist.sync.handler.CartMessageHandler
import de.moyapro.nushppinglist.sync.handler.ItemMessageHandler
import de.moyapro.nushppinglist.sync.messages.CartMessage
import de.moyapro.nushppinglist.sync.messages.ItemMessage
import de.moyapro.nushppinglist.sync.messages.RequestItemMessage
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.util.createSampleCartItem
import de.moyapro.nushppinglist.util.MainCoroutineRule
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.*
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
    private val publisher: Publisher = mockk(relaxed = true)
    private val cartDao: CartDaoMock =
        CartDaoMock(CoroutineScope(TestCoroutineDispatcher() + SupervisorJob()))

    @Before
    fun setup() {
        viewModel = CartViewModel(cartDao)
    }

    @After
    fun tearDown() {
        cartDao.reset()
    }

    @Test(timeout = 10_000)
    fun handleCartMessage__cartAndItemExists() = runBlocking {
        val cartItemList = listOf(
            createSampleCartItem(),
            createSampleCartItem()
        )
        cartItemList.forEach { viewModel.add(it) }
        val requestWithUpdatedCartItems = CartMessage(
            cartItemList
                .map { it.cartItemProperties }
                .map { it.copy(checked = true) }
        )
        CartMessageHandler(cartDao, publisher)(requestWithUpdatedCartItems)
        Thread.sleep(100) // wait for DB to save
        cartItemList.map { it.item.itemId }.forEach { itemId ->
            val resultItem = viewModel.getCartItemPropertiesByItemId(itemId)
            resultItem?.itemId shouldBe itemId
            resultItem?.checked shouldBe true
        }
    }

    @Test(timeout = 10_000)
    fun handleCartMessage__ItemExists() = runBlocking {
        val cartItemList = listOf(
            createSampleCartItem(),
            createSampleCartItem()
        )
        cartItemList.map { it.item }.forEach { viewModel.add(it) }
        val request = CartMessage(cartItemList.map { it.cartItemProperties })
        CartMessageHandler(cartDao, publisher)(request)
        Thread.sleep(100) // wait for DB to save
        cartItemList.map { it.item.itemId }.forEach { itemId ->
            val resultItem = viewModel.getCartItemPropertiesByItemId(itemId)
            resultItem?.itemId shouldBe itemId
        }
    }

    @Test(timeout = 10_000)
    fun handleCartMessage__NothingExists() {
        val cartItemList = listOf(
            createSampleCartItem(),
            createSampleCartItem()
        )
        val cartMessage = CartMessage(cartItemList.map { it.cartItemProperties })
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            CartMessageHandler(cartDao, publisher)(cartMessage)
        }
        Thread.sleep(100) // wait for itemrequests to be created
        cartItemList.map { it.item.itemId }.forEach { itemId ->
            verify { publisher.publish(RequestItemMessage(itemId)) }
        }
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            cartItemList.map { it.item }.forEach { item ->
                ItemMessageHandler(cartDao, publisher)(ItemMessage(item))
            }
        }
        Thread.sleep(500) // wait for itemrequests to be received and saved
        cartItemList.map { it.item.itemId }.forEach { itemId ->
            val resultItem = viewModel.getCartItemPropertiesByItemId(itemId)
            resultItem?.itemId shouldBe itemId
        }
    }

    @Test(timeout = 100_000)
    fun handleCartUpdateRequest__success() = runBlocking {
        val cartItem = createSampleCartItem()
        viewModel.add(cartItem)
        val updatedCartItemProperties = cartItem.cartItemProperties.copy(
            amount = cartItem.cartItemProperties.amount + 1,
            checked = !cartItem.cartItemProperties.checked
        )
        val request = CartMessage(updatedCartItemProperties)
        CartMessageHandler(cartDao, publisher)(request)
        Thread.sleep(1000) // wait for DB to save
        val result = viewModel.getCartItemPropertiesByItemId(cartItem.item.itemId)
        result?.amount shouldBe updatedCartItemProperties.amount
        result?.checked shouldBe updatedCartItemProperties.checked
    }
}

