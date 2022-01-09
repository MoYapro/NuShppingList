package de.moyapro.nushppinglist.sync

import de.moyapro.nushppinglist.MockPublisher
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.mock.CartDaoMock
import de.moyapro.nushppinglist.sync.handler.ItemMessageHandler
import de.moyapro.nushppinglist.sync.messages.ItemMessage
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.util.createSampleItem
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
class ItemMessageHandlerTest {

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
        val item = createSampleItem()
        val request = ItemMessage(item)
        ItemMessageHandler(viewModel, publisher)(request)
        Thread.sleep(100) // wait for DB to save
        val resultItem = viewModel.getItemByItemId(item.itemId)
        resultItem?.itemId.shouldBe(item.itemId)
        resultItem?.name shouldBe item.name
        resultItem?.description shouldBe item.description
        resultItem?.defaultItemAmount shouldBe item.defaultItemAmount
        resultItem?.defaultItemUnit shouldBe item.defaultItemUnit
        resultItem?.kategory shouldBe item.kategory
    }

}

