package de.moyapro.nushppinglist.sync

import de.moyapro.nushppinglist.MockPublisher
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.mock.CartDaoMock
import de.moyapro.nushppinglist.sync.handler.RequestItemMessageHandler
import de.moyapro.nushppinglist.sync.messages.RequestItemMessage
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.util.createSampleItem
import de.moyapro.nushppinglist.util.MainCoroutineRule
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
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
class RequestItemMessageHandlerTest {

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
        viewModel.add(item)
        val request = RequestItemMessage(item.itemId)
        val requestHandler = RequestItemMessageHandler(viewModel, publisher)
        requestHandler(request)
        Thread.sleep(100) // wait for DB to save
        publisher.messages[CONSTANTS.MQTT_TOPIC_ITEM] shouldContain item.itemId.id.toString()
        publisher.messages[CONSTANTS.MQTT_TOPIC_ITEM] shouldContain item.name
        publisher.messages[CONSTANTS.MQTT_TOPIC_ITEM] shouldContain item.description
        publisher.messages[CONSTANTS.MQTT_TOPIC_ITEM] shouldContain item.defaultItemAmount.toString()
        publisher.messages[CONSTANTS.MQTT_TOPIC_ITEM] shouldContain item.defaultItemUnit.toString()
        publisher.messages[CONSTANTS.MQTT_TOPIC_ITEM] shouldContain item.kategory.toString()
    }

    @Test(timeout = 10_000)
    fun handleItemRequest__itemNotFound() {
        val request = RequestItemMessage(ItemId())
        val requestHandler = RequestItemMessageHandler(viewModel, publisher)
        requestHandler(request)
        publisher.messages shouldBe emptyMap()
    }
}

