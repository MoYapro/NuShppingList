package de.moyapro.nushppinglist.sync

import com.fasterxml.jackson.module.kotlin.readValue
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.serialization.ConfiguredObjectMapper
import de.moyapro.nushppinglist.sync.handler.*
import java.nio.charset.StandardCharsets
import kotlin.concurrent.thread

class MessageHandler(
    private val requestItemMessageHandler: RequestItemMessageHandler,
    private val requestCartMessageHandler: RequestCartMessageHandler,
    private val itemMessageHandler: ItemMessageHandler,
    private val cartMessageHandler: CartMessageHandler,
    private val cartItemUpdateMessageHandler: CartItemUpdateMessageHandler,
) : (Mqtt5Publish) -> Unit {
    var lastItem: Any? = null
    private val objectMapper = ConfiguredObjectMapper()

    override fun invoke(message: Mqtt5Publish) {
        thread(start = true) {
            println("<==\t${message.topic}:\t ${String(message.payloadAsBytes)}")
            when (message.topic.toString()) {
                CONSTANTS.MQTT_TOPIC_ITEM_REQUEST -> requestItemMessageHandler(readMessage(message.payloadAsBytes))
                CONSTANTS.MQTT_TOPIC_ITEM -> itemMessageHandler(readMessage(message.payloadAsBytes))
                CONSTANTS.MQTT_TOPIC_CART_REQUEST -> requestCartMessageHandler(readMessage(message.payloadAsBytes))
                CONSTANTS.MQTT_TOPIC_CART -> cartMessageHandler(readMessage(message.payloadAsBytes))
                CONSTANTS.MQTT_TOPIC_CART_UPDATE -> cartItemUpdateMessageHandler(readMessage(message.payloadAsBytes))
                else -> throw IllegalArgumentException("Don't know how to handle topic ${message.topic}")
            }
        }
    }

    private inline fun <reified T> readMessage(payload: ByteArray): T {
        val messageObject: T =
            objectMapper.readValue(String(payload, StandardCharsets.UTF_8))
        lastItem = messageObject
        return messageObject
    }

}
