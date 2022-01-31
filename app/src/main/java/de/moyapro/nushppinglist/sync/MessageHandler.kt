package de.moyapro.nushppinglist.sync

import android.util.Log
import com.fasterxml.jackson.module.kotlin.readValue
import com.hivemq.client.mqtt.datatypes.MqttTopic
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.serialization.ConfiguredObjectMapper
import de.moyapro.nushppinglist.sync.handler.CartMessageHandler
import de.moyapro.nushppinglist.sync.handler.ItemMessageHandler
import de.moyapro.nushppinglist.sync.handler.RequestCartMessageHandler
import de.moyapro.nushppinglist.sync.handler.RequestItemMessageHandler
import de.moyapro.nushppinglist.sync.messages.RequestCartListMessage
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
    private val tag = MessageHandler::class.simpleName

    override fun invoke(message: Mqtt5Publish) {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            println("<==\t${message.topic}:\t ${String(message.payloadAsBytes)}")
            handleMessage(message.topic, message.payloadAsBytes)
        }
    }

    suspend fun handleMessage(topic: MqttTopic, messageBytes: ByteArray) {
        Log.i(tag, "${topic.levels}")
        when {
            topic matches CONSTANTS.MQTT_TOPIC_ITEM_REQUEST -> requestItemMessageHandler(readMessage(
                messageBytes))
            topic matches CONSTANTS.MQTT_TOPIC_ITEM -> itemMessageHandler(readMessage(messageBytes))
            topic matches CONSTANTS.MQTT_TOPIC_CART_REQUEST -> requestCartMessageHandler(readMessage(
                messageBytes))
            topic matches CONSTANTS.MQTT_TOPIC_CART -> cartMessageHandler(readMessage(messageBytes))
            topic matches CONSTANTS.MQTT_TOPIC_CARTLIST_REQUEST -> requestCartlistMessageHandler(
                readMessage(messageBytes))
            else -> throw IllegalArgumentException("Don't know how to handle topic $topic")
        }
    }

    private fun requestCartlistMessageHandler(requestCartListMessage: RequestCartListMessage) {
TODO()
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

infix fun MqttTopic.matches(topicString: String): Boolean {
    if(topicString.isBlank()) return false
    val otherTopicLevels = MqttTopic.of(topicString).levels
    return this.levels.containsAll(otherTopicLevels)
}
