package de.moyapro.nushppinglist.sync

import com.fasterxml.jackson.module.kotlin.readValue
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.serialization.ConfiguredObjectMapper
import de.moyapro.nushppinglist.sync.messages.*
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.nio.charset.StandardCharsets

class MessageHandler : (String, MqttMessage) -> Unit {
    var lastItem: Any? = null
    private val objectMapper = ConfiguredObjectMapper()

    override fun invoke(topic: String, message: MqttMessage) {
        when (topic) {
            CONSTANTS.MQTT_TOPIC_ITEM_REQUEST -> handleItemRequest(readMessage(message))
            CONSTANTS.MQTT_TOPIC_ITEM -> handleItemMessage(readMessage(message))
            CONSTANTS.MQTT_TOPIC_CART_REQUEST -> handleCartRequest(readMessage(message))
            CONSTANTS.MQTT_TOPIC_CART -> handleCartMessage(readMessage(message))
            CONSTANTS.MQTT_TOPIC_CART_UPDATE -> handleCartUpdate(readMessage(message))
            else -> throw IllegalArgumentException("Don't know how to handle topic $topic")
        }
    }

    private fun handleCartUpdate(cartItemUpdate: CartItemUpdateMessage) {
        lastItem = cartItemUpdate
    }

    private fun handleCartMessage(cartMessage: CartMessage) {
        lastItem = cartMessage
    }

    private fun handleItemMessage(itemMessage: ItemMessage) {
        lastItem = itemMessage
    }

    private fun handleCartRequest(requestCartMessage: RequestCartMessage) {
        lastItem = requestCartMessage
    }

    private inline fun <reified T> readMessage(message: MqttMessage): T =
        ConfiguredObjectMapper().readValue(String(message.payload, StandardCharsets.UTF_8))

    fun handleItemRequest(message: RequestItemMessage) {
        lastItem = message
    }
}
