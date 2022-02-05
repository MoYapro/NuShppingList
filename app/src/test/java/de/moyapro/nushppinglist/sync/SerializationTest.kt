package de.moyapro.nushppinglist.sync

import com.fasterxml.jackson.module.kotlin.readValue
import de.moyapro.nushppinglist.db.ids.CartId
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.db.model.*
import de.moyapro.nushppinglist.serialization.ConfiguredObjectMapper
import de.moyapro.nushppinglist.sync.messages.*
import de.moyapro.nushppinglist.ui.util.createSampleCartItem
import de.moyapro.nushppinglist.ui.util.createSampleItem
import io.kotest.matchers.shouldBe
import org.junit.Test
import java.util.*

class SerializationTest {

    private val objectMapper = ConfiguredObjectMapper()

    @Test
    fun itemMessage() {
        val expectedJson =
            """{"items":[{"itemId":"c871a987-54ac-4f88-8e57-054a3507db5a","name":"Sugar","description":"weiss","defaultItemAmount":1000,"defaultItemUnit":"GRAMM","price":0.00,"kategory":"SONSTIGES"}]}"""
        val instance = ItemMessage(createSampleItem())

        val actualJson = objectMapper.writeValueAsString(instance)
        actualJson shouldBe expectedJson
        val actualInstance: ItemMessage = objectMapper.readValue(actualJson)
        actualInstance shouldBe instance
    }

    @Test
    fun cartMessage() {
        val cartId = CartId(UUID.fromString("9bec54bd-86d2-4741-a89c-b167032adc2e"))
        val expectedJson =
            """{"cartItemPropertiesList":[{"cartItemPropertiesId":"5cf9d5fd-f181-4620-86ec-7a597219cb12","cartItemId":"c871a987-54ac-4f88-8e57-054a3507db5a","inCart":"9bec54bd-86d2-4741-a89c-b167032adc2e","itemId":"c871a987-54ac-4f88-8e57-054a3507db5a","recipeId":"86999915-5a24-46ca-8fc2-fb8b6efca219","amount":1000,"checked":false}],"cartId":"9bec54bd-86d2-4741-a89c-b167032adc2e"}"""
        val instance =
            CartMessage(
                listOf(
                    createSampleCartItem(RecipeId(UUID.fromString("86999915-5a24-46ca-8fc2-fb8b6efca219")),
                        inCart = cartId)
                ).map(CartItem::cartItemProperties),
                cartId
            )

        val actualJson = objectMapper.writeValueAsString(instance)
        actualJson shouldBe expectedJson
        val actualInstance: CartMessage = objectMapper.readValue(actualJson)
        actualInstance shouldBe instance
    }

    @Test
    fun cartMessageSimple() {
        val cartId = CartId(UUID.fromString("9bec54bd-86d2-4741-a89c-b167032adc2e"))
        val expectedJson =
            """{"cartItemPropertiesList":[],"cartId":"9bec54bd-86d2-4741-a89c-b167032adc2e"}"""
        val instance =
            CartMessage(
                listOf(),
                cartId
            )

        val actualJson = objectMapper.writeValueAsString(instance)
        actualJson shouldBe expectedJson
        val actualInstance: CartMessage = objectMapper.readValue(actualJson)
        actualInstance shouldBe instance
    }

    @Test
    fun requestCartMessage() {
        val cartId = CartId(UUID.fromString("22fee99d-2c10-45b7-a116-c5d37028dd67"))
        val expectedJson =
            """{"cartId":"22fee99d-2c10-45b7-a116-c5d37028dd67"}"""
        val instance = RequestCartMessage(cartId)

        val actualJson = objectMapper.writeValueAsString(instance)
        actualJson shouldBe expectedJson
        val actualInstance: RequestCartMessage = objectMapper.readValue(actualJson)
        actualInstance shouldBe instance
    }

    @Test
    fun requestItemMessage() {
        val expectedJson =
            """{"itemIds":["86999915-5a24-46ca-8fc2-fb8b6efca219","5cf9d5fd-f181-4620-86ec-7a597219cb12"]}"""
        val instance = RequestItemMessage(listOf(
            ItemId(UUID.fromString("86999915-5a24-46ca-8fc2-fb8b6efca219")),
            ItemId(UUID.fromString("5cf9d5fd-f181-4620-86ec-7a597219cb12")),
        ))

        val actualJson = objectMapper.writeValueAsString(instance)
        actualJson shouldBe expectedJson
        val actualInstance: RequestItemMessage = objectMapper.readValue(actualJson)
        actualInstance shouldBe instance
    }

    @Test
    fun requestCartListMessage() {
        val expectedJson =
            """{"message":"some message"}"""
        val instance = RequestCartListMessage("some message")

        val actualJson = objectMapper.writeValueAsString(instance)
        actualJson shouldBe expectedJson
        val actualInstance: RequestCartListMessage = objectMapper.readValue(actualJson)
        actualInstance shouldBe instance

    }

    @Test
    fun item() {
        val instance = Item()

        val actualJson = objectMapper.writeValueAsString(instance)
        val actualInstance: Item = objectMapper.readValue(actualJson)
        actualInstance shouldBe instance
    }

    @Test
    fun cartItemProperties() {
        val instance = CartItemProperties()

        val actualJson = objectMapper.writeValueAsString(instance)
        val actualInstance: CartItemProperties = objectMapper.readValue(actualJson)
        actualInstance shouldBe instance
    }

    @Test
    fun cartlistMessage() {
        val expectedJson =
            """{"carts":[{"cartId":"5e90918f-ebad-4328-9c18-4e94bb64b9c5","cartName":"Cart1","synced":true,"selected":false}]}"""
        val instance = CartListMessage(listOf(Cart(
            cartId = CartId(UUID.fromString("5e90918f-ebad-4328-9c18-4e94bb64b9c5")),
            cartName = "Cart1",
            synced = true,
            selected = false,
        )))

        val actualJson = objectMapper.writeValueAsString(instance)
        actualJson shouldBe expectedJson
        val actualInstance: CartListMessage = objectMapper.readValue(actualJson)
        actualInstance shouldBe instance
    }


}
