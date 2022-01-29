package de.moyapro.nushppinglist.sync

import com.fasterxml.jackson.module.kotlin.readValue
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.mock.CartDaoMock
import de.moyapro.nushppinglist.serialization.ConfiguredObjectMapper
import de.moyapro.nushppinglist.sync.handler.RequestItemMessageHandler
import de.moyapro.nushppinglist.sync.messages.RequestItemMessage
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.util.createSampleItem
import de.moyapro.nushppinglist.util.MainCoroutineRule
import de.moyapro.nushppinglist.util.test.MockPublisher
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
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
    fun handleItemRequest__success() = runBlocking {
        val item = createSampleItem()
        viewModel.add(item)
        val request = RequestItemMessage(item.itemId)
        val requestHandler = RequestItemMessageHandler(cartDao, publisher)
        requestHandler(request)
        Thread.sleep(1000) // wait for DB to save
        publisher.messages[CONSTANTS.MQTT_TOPIC_ITEM] shouldContain item.itemId.id.toString()
        publisher.messages[CONSTANTS.MQTT_TOPIC_ITEM] shouldContain item.name
        publisher.messages[CONSTANTS.MQTT_TOPIC_ITEM] shouldContain item.description
        publisher.messages[CONSTANTS.MQTT_TOPIC_ITEM] shouldContain item.defaultItemAmount.toString()
        publisher.messages[CONSTANTS.MQTT_TOPIC_ITEM] shouldContain item.defaultItemUnit.toString()
        publisher.messages[CONSTANTS.MQTT_TOPIC_ITEM] shouldContain item.kategory.toString()
        Unit
    }

    @Test(timeout = 10_000)
    fun handleItemRequest__itemNotFound() = runBlocking {
        val request = RequestItemMessage(ItemId())
        val requestHandler = RequestItemMessageHandler(cartDao, publisher)
        requestHandler(request)
        publisher.messages shouldBe emptyMap()
    }

    @Test
    fun deserialize_requestItemMessage() = runBlocking {
        val requestHandler = RequestItemMessageHandler(cartDao, publisher)
        val messageContent =
            """{"itemIds":["07329ba4-a378-495e-8233-ab7ed340842a"]}""".toByteArray()
        val result = requestHandler(ConfiguredObjectMapper().readValue(messageContent))

        result shouldNotBe null
    }

}

