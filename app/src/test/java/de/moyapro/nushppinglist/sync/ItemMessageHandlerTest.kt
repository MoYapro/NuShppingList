package de.moyapro.nushppinglist.sync

import de.moyapro.nushppinglist.MockPublisher
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.constants.KATEGORY
import de.moyapro.nushppinglist.constants.UNIT
import de.moyapro.nushppinglist.db.dao.getItemByItemId
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.mock.CartDaoMock
import de.moyapro.nushppinglist.sync.handler.ItemMessageHandler
import de.moyapro.nushppinglist.sync.messages.ItemMessage
import de.moyapro.nushppinglist.ui.util.createSampleItem
import de.moyapro.nushppinglist.util.MainCoroutineRule
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal


@Suppress("EXPERIMENTAL_API_USAGE")
@ExperimentalCoroutinesApi
class ItemMessageHandlerTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var publisher: MockPublisher
    private val cartDao: CartDaoMock =
        CartDaoMock(CoroutineScope(TestCoroutineDispatcher() + SupervisorJob()))

    @Before
    fun setup() {
        publisher = MockPublisher(CONSTANTS.MQTT_TOPIC_ITEM)
    }

    @After
    fun tearDown() {
        cartDao.reset()
        publisher.reset()
    }

    @Test(timeout = 10_000)
    fun handleItemRequest() = runBlocking {
        val item = createSampleItem()
        val request = ItemMessage(item)
        ItemMessageHandler(cartDao, publisher)(request)
        Thread.sleep(100) // wait for DB to save
        val resultItem = cartDao.getItemByItemId(item.itemId)
        resultItem?.itemId.shouldBe(item.itemId)
        resultItem?.name shouldBe item.name
        resultItem?.description shouldBe item.description
        resultItem?.defaultItemAmount shouldBe item.defaultItemAmount
        resultItem?.defaultItemUnit shouldBe item.defaultItemUnit
        resultItem?.kategory shouldBe item.kategory
    }

    @Test(timeout = 10_000)
    fun handleItemRequest__receiveTwice() = runBlocking {
        val item = createSampleItem()
        val request = ItemMessage(item)
        ItemMessageHandler(cartDao, publisher)(request)
        ItemMessageHandler(cartDao, publisher)(request)
        Thread.sleep(100) // wait for DB to save
        val resultItem = cartDao.getItemByItemId(item.itemId)
        resultItem?.itemId.shouldBe(item.itemId)
        resultItem?.name shouldBe item.name
        resultItem?.description shouldBe item.description
        resultItem?.defaultItemAmount shouldBe item.defaultItemAmount
        resultItem?.defaultItemUnit shouldBe item.defaultItemUnit
        resultItem?.kategory shouldBe item.kategory
    }

    @Test(timeout = 10_000)
    fun handleItemRequest__receiveDuplicateName__keep() = runBlocking {
        val handler = ItemMessageHandler(cartDao, publisher)
        val itemInDb = Item(
            name = "name",
            description = "description1",
            defaultItemAmount = 2,
            defaultItemUnit = UNIT.LITER,
            price = BigDecimal(2),
            kategory = KATEGORY.GEMUESE
        )
        val itemFromMessage = Item(
            name = "name",
            description = "description2",
            defaultItemAmount = 3,
            defaultItemUnit = UNIT.LITER,
            price = BigDecimal(3),
            kategory = KATEGORY.SONSTIGES,
        )
        cartDao.save(itemInDb)

        handler(ItemMessage(itemFromMessage))

        Thread.sleep(100) // wait for DB to save
        val resultItem = cartDao.getItemByItemId(itemInDb.itemId)
        resultItem?.itemId.shouldBe(itemInDb.itemId)
        resultItem?.name shouldBe itemInDb.name
        resultItem?.description shouldBe itemInDb.description
        resultItem?.defaultItemAmount shouldBe itemInDb.defaultItemAmount
        resultItem?.defaultItemUnit shouldBe itemInDb.defaultItemUnit
        resultItem?.kategory shouldBe itemInDb.kategory
    }

    @Test(timeout = 10_000)
    fun handleItemRequest__receiveDuplicateName__update() = runBlocking {
        val handler = ItemMessageHandler(cartDao, publisher)
        val itemInDb = Item(
            name = "name",
            description = "",
            defaultItemAmount = 1,
            defaultItemUnit = UNIT.UNSPECIFIED,
            price = BigDecimal.ZERO.setScale(2),
            kategory = KATEGORY.SONSTIGES
        )
        val itemFromMessage = Item(
            name = "name",
            description = "description2",
            defaultItemAmount = 3,
            defaultItemUnit = UNIT.LITER,
            price = BigDecimal(3),
            kategory = KATEGORY.GEMUESE
        )
        cartDao.save(itemInDb)

        handler(ItemMessage(itemFromMessage))

        Thread.sleep(100) // wait for DB to save
        val resultItem = cartDao.getItemByItemId(itemInDb.itemId)
        resultItem?.itemId shouldNotBe itemFromMessage.itemId
        resultItem?.name shouldBe itemFromMessage.name
        resultItem?.description shouldBe itemFromMessage.description
        resultItem?.defaultItemAmount shouldBe itemFromMessage.defaultItemAmount
        resultItem?.defaultItemUnit shouldBe itemFromMessage.defaultItemUnit
        resultItem?.kategory shouldBe itemFromMessage.kategory
    }

    @Test(timeout = 10_000)
    fun handleItemRequest__receiveMany() = runBlocking {
        val handler = ItemMessageHandler(cartDao, publisher)
        val uniqueItems = (1..1000).map { createSampleItem(name = it.toString()) }
        val items = (uniqueItems.shuffled() + uniqueItems.shuffled()).shuffled()
        items.parallelStream()
            .map { ItemMessage(it) }
            .forEach { itemMessage ->
                CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
                    handler(itemMessage)
                }
            }
        Thread.sleep(1000)
        items.forEach { cartDao.getItemByItemId(it.itemId) shouldBe it }
    }

}

