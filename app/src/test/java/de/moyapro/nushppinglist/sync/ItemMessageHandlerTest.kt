package de.moyapro.nushppinglist.sync

import de.moyapro.nushppinglist.constants.KATEGORY
import de.moyapro.nushppinglist.constants.UNIT
import de.moyapro.nushppinglist.db.dao.getItemByItemId
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.mock.CartDaoMock
import de.moyapro.nushppinglist.sync.handler.ItemMessageHandler
import de.moyapro.nushppinglist.sync.messages.ItemMessage
import de.moyapro.nushppinglist.ui.util.createSampleItem
import de.moyapro.nushppinglist.util.MainCoroutineRule
import de.moyapro.nushppinglist.util.test.MockPublisher
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.After
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal


@Suppress("EXPERIMENTAL_API_USAGE")
@ExperimentalCoroutinesApi
class ItemMessageHandlerTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val cartDao: CartDaoMock =
        CartDaoMock(CoroutineScope(TestCoroutineDispatcher() + SupervisorJob()))


    @After
    fun tearDown() {
        cartDao.reset()
        MockPublisher.reset()
    }

    @Test(timeout = 10_000)
    fun handleItemRequest(): Unit = runBlocking {
        val item = createSampleItem()
        val request = ItemMessage(item)
        ItemMessageHandler(cartDao, MockPublisher)(request)
        Thread.sleep(100)
        val resultItem = cartDao.getItemByItemId(item.itemId)
        resultItem?.itemId.shouldBe(item.itemId)
        resultItem?.name shouldBe item.name
        resultItem?.description shouldBe item.description
        resultItem?.defaultItemAmount shouldBe item.defaultItemAmount
        resultItem?.defaultItemUnit shouldBe item.defaultItemUnit
        resultItem?.kategory shouldBe item.kategory
    }

    @Test(timeout = 10_000)
    fun handleItemRequest__receiveTwice(): Unit = runBlocking {
        val item = createSampleItem()
        val request = ItemMessage(item)
        ItemMessageHandler(cartDao, MockPublisher)(request)
        ItemMessageHandler(cartDao, MockPublisher)(request)
        Thread.sleep(100)
        val resultItem = cartDao.getItemByItemId(item.itemId)
        resultItem?.itemId.shouldBe(item.itemId)
        resultItem?.name shouldBe item.name
        resultItem?.description shouldBe item.description
        resultItem?.defaultItemAmount shouldBe item.defaultItemAmount
        resultItem?.defaultItemUnit shouldBe item.defaultItemUnit
        resultItem?.kategory shouldBe item.kategory
    }

    @Test(timeout = 10_000)
    fun handleItemRequest__receiveDuplicateName__keep(): Unit = runBlocking {
        val handler = ItemMessageHandler(cartDao, MockPublisher)
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
            kategory = KATEGORY.BROTBELAG,
        )
        cartDao.save(itemInDb)
        Thread.sleep(100)

        handler(ItemMessage(itemFromMessage))
        Thread.sleep(100)
        cartDao.getItemByItemId(itemInDb.itemId) shouldBe itemFromMessage.apply { itemId = itemInDb.itemId } // id is not overwritten
    }

    @Test(timeout = 10000_000)
    fun handleItemRequest__receiveDuplicateName__update(): Unit = runBlocking {
        val handler = ItemMessageHandler(cartDao, MockPublisher)
        val itemInDb = Item(
            name = "name",
            description = "overwrite me",
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

        Thread.sleep(200)
        val resultItem = cartDao.getItemByItemId(itemInDb.itemId)
        resultItem?.itemId shouldNotBe itemFromMessage.itemId
        resultItem?.name shouldBe itemFromMessage.name
        resultItem?.description shouldBe itemFromMessage.description
        resultItem?.defaultItemAmount shouldBe itemFromMessage.defaultItemAmount
        resultItem?.defaultItemUnit shouldBe itemFromMessage.defaultItemUnit
        resultItem?.kategory shouldBe itemFromMessage.kategory
    }

    @Test(timeout = 10_000)
    fun mergeItems(): Unit = runBlocking {
        val handler = ItemMessageHandler(cartDao, MockPublisher)
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

        val resultItem = handler.merge(itemInDb, itemFromMessage)
        resultItem.itemId shouldNotBe itemFromMessage.itemId
        resultItem.name shouldBe itemFromMessage.name
        resultItem.description shouldBe itemFromMessage.description
        resultItem.defaultItemAmount shouldBe itemFromMessage.defaultItemAmount
        resultItem.defaultItemUnit shouldBe itemFromMessage.defaultItemUnit
        resultItem.kategory shouldBe itemFromMessage.kategory
    }

    @Test(timeout = 10_000)
    fun handleItemRequest__receiveMany(): Unit = runBlocking {
        val handler = ItemMessageHandler(cartDao, MockPublisher)
        val uniqueItems = (1..20).map { createSampleItem(name = "itemname") }
        val items = (uniqueItems.shuffled() + uniqueItems.shuffled()).shuffled()
        items.parallelStream()
            .map { ItemMessage(it) }
            .forEach { itemMessage ->
                CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
                    handler(itemMessage)
                }
            }
        Thread.sleep(5000)
        items.forEach { cartDao.getItemByItemId(it.itemId) shouldBe it }
    }

}

