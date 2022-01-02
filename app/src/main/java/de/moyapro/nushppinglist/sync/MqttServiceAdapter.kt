package de.moyapro.nushppinglist.sync


import android.content.Context
import android.util.Log
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.serialization.ConfiguredObjectMapper
import de.moyapro.nushppinglist.sync.messages.ShoppingMessage
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.nio.charset.StandardCharsets


class MqttServiceAdapter private constructor(
    private val mqttClient: IMqttAsyncClient,
    messageHandler: (String, MqttMessage) -> Unit,
) : Publisher {
    private val tag = "MqttClient"

    companion object Builder {
        fun createMqttServiceAdapter(
            context: Context,
            mqttConnectOptions: MqttConnectOptions,
            clientIdSuffix: String = "",
            messageHandler: (String, MqttMessage) -> Unit = { _, _ -> },
        ) = MqttServiceAdapter(
            MqttAndroidClient(
                context,
                mqttConnectOptions.serverURIs[0],
                "NuShoppingListServiceClient_$clientIdSuffix"
            ),
            messageHandler
        )

        fun createMqttAdapter(
            mqttConnectOptions: MqttConnectOptions,
            clientIdSuffix: String = "",
            messageHandler: (String, MqttMessage) -> Unit = { _, _ -> },
        ) = MqttServiceAdapter(
            MqttAsyncClient(
                mqttConnectOptions.serverURIs[0],
                "NuShoppingListClient_$clientIdSuffix",
                MemoryPersistence()
            ),
            messageHandler
        )
    }

    private var isConnected = false
    fun isConnected() = isConnected


    fun connect(): MqttServiceAdapter {
        mqttClient.connect(MqttConnectOptions(), null, MqttActionListener { isConnected = true })
        return this
    }

    fun disconnect() {
        if (!isConnected) {
            return
        }
        mqttClient.disconnect(null, MqttActionListener { isConnected = false })
    }

    fun subscribe(topic: String) {
        mqttClient.subscribe(topic, 0)
    }

    fun unsubscribe(topic: String) {
        mqttClient.unsubscribe(topic)
    }

    override fun publish(messageObject: ShoppingMessage) {
        val topic = CONSTANTS.messagesWithTopic[messageObject::class]
        require(topic != null) { "Could not find topic for $messageObject" }
        mqttClient.publish(topic,
            ConfiguredObjectMapper().writeValueAsString(messageObject)
                .toByteArray(StandardCharsets.UTF_8),
            0,
            false)
    }

    fun setHandler(messageHandler: MessageHandler) {
        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String) {
                isConnected = true
                Log.i(tag, "connectComplete with serverURI: $serverURI")
            }

            override fun connectionLost(cause: Throwable?) {
                isConnected = false
                Log.w(tag,
                    cause?.message ?: "Connection Lost without error message")
                cause?.printStackTrace()
            }

            override fun messageArrived(topic: String, message: MqttMessage) {
                Log.i(tag, "messageArraived: $message")
                messageHandler(topic, message)
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                Log.i(tag, "deliveryComplete: $token")
            }
        })

    }
}
