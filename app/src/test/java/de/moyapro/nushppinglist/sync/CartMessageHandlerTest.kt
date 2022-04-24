package de.moyapro.nushppinglist.sync

import com.hivemq.client.mqtt.datatypes.MqttTopic
import de.moyapro.nushppinglist.db.ids.CartId
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.db.model.*
import de.moyapro.nushppinglist.mock.CartDaoMock
import de.moyapro.nushppinglist.sync.handler.CartMessageHandler
import de.moyapro.nushppinglist.sync.messages.CartMessage
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.util.createSampleCartItem
import de.moyapro.nushppinglist.util.MainCoroutineRule
import de.moyapro.nushppinglist.util.test.MockPublisher
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*


@Suppress("EXPERIMENTAL_API_USAGE")
@ExperimentalCoroutinesApi
class CartMessageHandlerTest {

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
    fun handleCartMessage__cartAndItemExists(): Unit = runBlocking {
        val cart = Cart()
        val cartItemList = listOf(
            CartItem("item1"),
            CartItem("item2")
        )
        viewModel.add(cart)
        Thread.sleep(100)
        cartItemList.forEach {
            it.cartItemProperties.inCart = cart.cartId
            viewModel.add(it)
        }
        Thread.sleep(100)
        val requestWithUpdatedCartItems = CartMessage(
            cartItemList
                .map { it.cartItemProperties }
                .map { it.copy(checked = true) },
            cart.cartId,
        )
        CartMessageHandler(cartDao)(requestWithUpdatedCartItems)
        Thread.sleep(100)
        cartDao.cartItemPropertiesTable shouldHaveSize 2
        cartItemList.map { it.item.itemId }.forEach { itemId ->
            val resultItem = cartDao.cartItemPropertiesTable.single { it.itemId == itemId }
            resultItem.itemId shouldBe itemId
            resultItem.checked shouldBe true
        }
    }

    @Test(timeout = 10_000)
    fun handleCartMessage__ItemExists(): Unit = runBlocking {
        val cart = Cart()
        val cartItemList = listOf(
            createSampleCartItem(),
            createSampleCartItem()
        )
        viewModel.add(cart)
        cartItemList.map { it.item }.forEach {
            viewModel.add(it)
        }
        val request = CartMessage(cartItemList.map { it.cartItemProperties }, cart.cartId)
        CartMessageHandler(cartDao, MockPublisher)(request)
        Thread.sleep(100)
        cartItemList.map { it.item.itemId }.forEach { itemId ->
            val resultItem = viewModel.getCartItemPropertiesByItemId(itemId)
            resultItem?.itemId shouldBe itemId
        }
    }

    @Test(timeout = 10_000)
    fun handleCartRequest__success(): Unit = runBlocking {
        val cart = Cart()
        val cartItem = createSampleCartItem().apply { cartItemProperties.inCart = cart.cartId }
        val updatedCartItemProperties = cartItem.cartItemProperties.copy(
            amount = cartItem.cartItemProperties.amount + 1,
            checked = !cartItem.cartItemProperties.checked
        )
        viewModel.add(cart)
        viewModel.add(cartItem)
        Thread.sleep(100)
        val request = CartMessage(listOf(updatedCartItemProperties), cart.cartId)
        val cartMessageHandler = CartMessageHandler(cartDao, MockPublisher)
        cartMessageHandler(request)
        Thread.sleep(100)
        with(cartDao.cartItemPropertiesTable.single()) {
            this shouldNotBe null
            this.amount shouldBe updatedCartItemProperties.amount
            this.checked shouldBe updatedCartItemProperties.checked
        }
    }

    @Test(timeout = 10_000)
    fun handleCartRequest__success_removeLast(): Unit = runBlocking {
        val cart = Cart()
        val cartItem = createSampleCartItem()
        viewModel.add(cart)
        viewModel.add(cartItem)
        Thread.sleep(100)
        val zeroCartItemProperties = cartItem.cartItemProperties.copy(amount = 0)
        val request = CartMessage(listOf(zeroCartItemProperties), cart.cartId)
        CartMessageHandler(cartDao)(request)
        Thread.sleep(100)
        val result = viewModel.getCartItemPropertiesByItemId(cartItem.item.itemId)
        result shouldBe null
    }

    @Test
    fun matches() {
        val topic = MqttTopic.of("a/b/c")
        topic matches "a" shouldBe true
        topic matches "b" shouldBe true
        topic matches "b/b" shouldBe true
        topic matches "a/b" shouldBe true
        topic matches "a/c" shouldBe true
        topic matches "b/c" shouldBe true
        topic matches "a/b/c" shouldBe true
        topic matches "" shouldBe false
        topic matches "x" shouldBe false
        topic matches "a/x" shouldBe false
        topic matches "a/x/c" shouldBe false
    }

    @Test
    fun merge__overwrites() {
        val handler = CartMessageHandler(cartDao, MockPublisher)
        val originalCartItemProperties = CartItemProperties()
        val updatedCartItemProperties = CartItemProperties(
            cartItemPropertiesId = UUID.randomUUID(),
            cartItemId = UUID.randomUUID(),
            inCart = CartId(),
            itemId = ItemId(),
            recipeId = RecipeId(),
            amount = 32,
            checked = true,
        )
        val result: CartItemProperties =
            handler.merge(originalCartItemProperties, updatedCartItemProperties)

        result.cartItemPropertiesId shouldBe originalCartItemProperties.cartItemPropertiesId
        result.cartItemId shouldBe updatedCartItemProperties.cartItemId
        result.inCart shouldBe updatedCartItemProperties.inCart
        result.itemId shouldBe updatedCartItemProperties.itemId
        result.recipeId shouldBe updatedCartItemProperties.recipeId
        result.amount shouldBe updatedCartItemProperties.amount
        result.checked shouldBe updatedCartItemProperties.checked

    }


    @Test
    fun merge__keeps() {
        val handler = CartMessageHandler(cartDao, MockPublisher)
        val originalCartItemProperties = CartItemProperties(
            cartItemPropertiesId = UUID.randomUUID(),
            cartItemId = UUID.randomUUID(),
            inCart = CartId(),
            itemId = ItemId(),
            recipeId = RecipeId(),
            amount = 32,
            checked = true,
        )
        val updatedCartItemProperties =
            CartItemProperties().copy(inCart = originalCartItemProperties.inCart)
        val result: CartItemProperties =
            handler.merge(originalCartItemProperties, updatedCartItemProperties)

        result.cartItemPropertiesId shouldBe originalCartItemProperties.cartItemPropertiesId
        result.cartItemId shouldBe updatedCartItemProperties.cartItemId
        result.inCart shouldBe updatedCartItemProperties.inCart
        result.itemId shouldBe updatedCartItemProperties.itemId
        result.recipeId shouldBe updatedCartItemProperties.recipeId
        result.amount shouldBe originalCartItemProperties.amount
        result.checked shouldBe updatedCartItemProperties.checked // checked is always used
    }

    @Test
    fun insertCartItemPropertiesWithIdConflict(): Unit = runBlocking {
        val handler = CartMessageHandler(cartDao, MockPublisher)
        val cartItemPropertiesToInsert = CartItemProperties(newItemId = ItemId(), amount = 12)
        val item = Item()
        val conflictingPropertiesInDb =
            CartItem(
                cartItemProperties = cartItemPropertiesToInsert.copy(
                    itemId = item.itemId,
                    amount = 9
                ), item = item
            )
        cartDao.save(conflictingPropertiesInDb.item)
        cartDao.save(conflictingPropertiesInDb.cartItemProperties)
        Thread.sleep(100)
        cartDao.cartItemPropertiesTable shouldHaveSize 1

        handler(CartMessage(listOf(cartItemPropertiesToInsert)))

        Thread.sleep(100)
        cartDao.cartItemPropertiesTable shouldHaveSize 1
    }

}

