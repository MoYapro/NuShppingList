package de.moyapro.nushppinglist.sync

import com.fasterxml.jackson.module.kotlin.readValue
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.serialization.ConfiguredObjectMapper
import de.moyapro.nushppinglist.sync.handler.CartMessageHandler
import de.moyapro.nushppinglist.sync.handler.ItemMessageHandler
import de.moyapro.nushppinglist.sync.handler.RequestCartMessageHandler
import de.moyapro.nushppinglist.sync.handler.RequestItemMessageHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets

class MessageHandler(
    private val requestItemMessageHandler: RequestItemMessageHandler,
    private val requestCartMessageHandler: RequestCartMessageHandler,
    private val itemMessageHandler: ItemMessageHandler,
    private val cartMessageHandler: CartMessageHandler,
) : (Mqtt5Publish) -> Unit {
    var lastItem: Any? = null
    private val objectMapper = ConfiguredObjectMapper()

    override fun invoke(message: Mqtt5Publish) {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            println("<==\t${message.topic}:\t ${String(message.payloadAsBytes)}")
            handleMessage(message.topic.toString(), message.payloadAsBytes)
        }
    }

    suspend fun handleMessage(topic: String, messageBytes: ByteArray) {
        when (topic) {
            CONSTANTS.MQTT_TOPIC_ITEM_REQUEST -> requestItemMessageHandler(String(messageBytes))
            CONSTANTS.MQTT_TOPIC_ITEM -> itemMessageHandler(readMessage(messageBytes))
            CONSTANTS.MQTT_TOPIC_CART_REQUEST -> requestCartMessageHandler(readMessage(messageBytes))
            CONSTANTS.MQTT_TOPIC_CART -> cartMessageHandler(readMessage(messageBytes))
            else -> throw IllegalArgumentException("Don't know how to handle topic $topic")
        }
    }

    companion object Builder {
        fun build(publisher: Publisher, cartDao: CartDao) =
            MessageHandler(
                requestItemMessageHandler = RequestItemMessageHandler(cartDao, publisher),
                requestCartMessageHandler = RequestCartMessageHandler(cartDao, publisher),
                itemMessageHandler = ItemMessageHandler(cartDao, publisher),
                cartMessageHandler = CartMessageHandler(cartDao, publisher),
            )

    }

    private inline fun <reified T> readMessage(payload: ByteArray): T {
        val messageObject: T =
            objectMapper.readValue(String(payload, StandardCharsets.UTF_8))
        lastItem = messageObject
        return messageObject
    }

}
