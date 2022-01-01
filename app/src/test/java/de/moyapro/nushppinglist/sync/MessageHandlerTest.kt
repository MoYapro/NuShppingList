package de.moyapro.nushppinglist.sync

import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.sync.messages.*
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.nio.charset.StandardCharsets
import kotlin.reflect.KClass

@RunWith(Parameterized::class)
class MessageHandlerTest(
    val topic: String,
    val message: MqttMessage,
    val expectedMessageType: KClass<*>,
) {


    companion object {

        val itemRequest =
            """{"itemId":"6565fd09-41fe-4c66-b1bb-9fac9e6ef794"}""".toByteArray(StandardCharsets.UTF_8)
        val cartRequest =
            """"This is a cart request"""".toByteArray(StandardCharsets.UTF_8)
        val itemMessage =
            """{"item":{"itemId":"a52fbe65-1808-4870-b56c-9505a31a2f41","name":"Ding","description":"An item named Ding","defaultItemAmount":12,"defaultItemUnit":"LITER","price":12.99,"kategory":"GEMUESE"}}"""
                .toByteArray(StandardCharsets.UTF_8)
        val cartMessage =
            """{"cartItemPropertiesList":[{"cartItemPropertiesId":"e896dfda-a20b-4796-ae73-63138b2ea84f","cartItemId":"142bf6cd-e164-40fb-b734-987946057d5c","itemId":"142bf6cd-e164-40fb-b734-987946057d5c","recipeId":"d84acfef-8a25-4e97-b7a9-73a2ea418076","amount":1000,"checked":false},{"cartItemPropertiesId":"f7a340aa-862f-4712-bcae-9398835828d2","cartItemId":"6fc527a1-0aa8-4af7-a21a-43e4a4b8b53d","itemId":"6fc527a1-0aa8-4af7-a21a-43e4a4b8b53d","recipeId":"ef2db8dd-af68-4360-967b-c37e05d6b2b1","amount":1000,"checked":false}]}"""
                .toByteArray(StandardCharsets.UTF_8)
        val cartItemPropertiesUpdate =
            """{"cartItemProperties":{"cartItemPropertiesId":"b9ea6adb-edf8-4339-956b-a30a38f7e159","cartItemId":"fd4a7edc-2d5b-4f85-a6be-82f362115896","itemId":"fd4a7edc-2d5b-4f85-a6be-82f362115896","recipeId":"c6379e77-fc9e-4de7-b2bf-9df88b398621","amount":1000,"checked":false}}"""
                .toByteArray(StandardCharsets.UTF_8)


        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                arrayOf(

                    CONSTANTS.MQTT_TOPIC_ITEM_REQUEST,
                    MqttMessage(itemRequest),
                    RequestItemMessage::class
                ),
                arrayOf(
                    CONSTANTS.MQTT_TOPIC_ITEM,
                    MqttMessage(itemMessage),
                    ItemMessage::class
                ),
                arrayOf(
                    CONSTANTS.MQTT_TOPIC_CART_REQUEST,
                    MqttMessage(cartRequest),
                    RequestCartMessage::class
                ),
                arrayOf(
                    CONSTANTS.MQTT_TOPIC_CART,
                    MqttMessage(cartMessage),
                    CartMessage::class
                ),
                arrayOf(
                    CONSTANTS.MQTT_TOPIC_CART_UPDATE,
                    MqttMessage(cartItemPropertiesUpdate),
                    CartItemUpdateMessage::class
                ),
            )
        }
    }

    @Test
    fun handleMessage() {
        val messageHandler = MessageHandler()
        messageHandler(topic, message)
        messageHandler.lastItem should { actual ->
            actual shouldNotBe null
            actual!!::class.qualifiedName shouldBe expectedMessageType.qualifiedName
        }

    }
}
