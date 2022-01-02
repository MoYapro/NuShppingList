package de.moyapro.nushppinglist.sync

import com.fasterxml.jackson.module.kotlin.readValue
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.serialization.ConfiguredObjectMapper
import de.moyapro.nushppinglist.sync.handler.*
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.nio.charset.StandardCharsets

class MessageHandler(
    private val requestItemMessageHandler: RequestItemMessageHandler,
    private val requestCartMessageHandler: RequestCartMessageHandler,
    private val itemMessageHandler: ItemMessageHandler,
    private val cartMessageHandler: CartMessageHandler,
    private val cartItemUpdateMessageHandler: CartItemUpdateMessageHandler,
) : (String, MqttMessage) -> Unit {
    var lastItem: Any? = null
    private val objectMapper = ConfiguredObjectMapper()

    override fun invoke(topic: String, message: MqttMessage) {
        when (topic) {
            CONSTANTS.MQTT_TOPIC_ITEM_REQUEST -> requestItemMessageHandler(readMessage(message))
            CONSTANTS.MQTT_TOPIC_ITEM -> itemMessageHandler(readMessage(message))
            CONSTANTS.MQTT_TOPIC_CART_REQUEST -> requestCartMessageHandler(readMessage(message))
            CONSTANTS.MQTT_TOPIC_CART -> cartMessageHandler(readMessage(message))
            CONSTANTS.MQTT_TOPIC_CART_UPDATE -> cartItemUpdateMessageHandler(readMessage(message))
            else -> throw IllegalArgumentException("Don't know how to handle topic $topic")
        }
    }

    private inline fun <reified T> readMessage(message: MqttMessage): T {
        val messageObject: T =
            objectMapper.readValue(String(message.payload, StandardCharsets.UTF_8))
        lastItem = messageObject
        return messageObject
    }

}
