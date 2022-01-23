package de.moyapro.nushppinglist.sync

import com.fasterxml.jackson.module.kotlin.readValue
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.RecipeId
import de.moyapro.nushppinglist.serialization.ConfiguredObjectMapper
import de.moyapro.nushppinglist.sync.messages.CartMessage
import de.moyapro.nushppinglist.sync.messages.ItemMessage
import de.moyapro.nushppinglist.sync.messages.RequestCartMessage
import de.moyapro.nushppinglist.sync.messages.RequestItemMessage
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
        val expectedJson =
            """{"cartItemPropertiesList":[{"cartItemPropertiesId":"5cf9d5fd-f181-4620-86ec-7a597219cb12","cartItemId":"c871a987-54ac-4f88-8e57-054a3507db5a","itemId":"c871a987-54ac-4f88-8e57-054a3507db5a","recipeId":"86999915-5a24-46ca-8fc2-fb8b6efca219","amount":1000,"checked":false}]}"""
        val instance =
            CartMessage(
                listOf(
                    createSampleCartItem(RecipeId(UUID.fromString("86999915-5a24-46ca-8fc2-fb8b6efca219")))
                ).map(CartItem::cartItemProperties)
            )

        val actualJson = objectMapper.writeValueAsString(instance)
        actualJson shouldBe expectedJson
        val actualInstance: CartMessage = objectMapper.readValue(actualJson)
        actualInstance shouldBe instance
    }

    @Test
    fun requestCartMessage() {
        val expectedJson =
            """{"message":"Can I get your cart, please?"}"""
        val instance = RequestCartMessage()

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

}
