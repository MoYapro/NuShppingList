package de.moyapro.nushppinglist.sync


import android.util.Log
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.serialization.ConfiguredObjectMapper
import de.moyapro.nushppinglist.sync.messages.ShoppingMessage
import java.util.*


class MqttServiceAdapter(
    clientIdSuffix: String = "",
    private var messageHandler: (Mqtt5Publish) -> Unit = {},
) : Publisher {
    private val tag = MqttServiceAdapter::class.simpleName
    private val mqttClient: Mqtt5AsyncClient
    private var isConnected = false
    fun isConnected() = isConnected

    init {
        Log.i(tag, "init")
        mqttClient =
            MqttClient.builder()
                .useMqttVersion5()
                .identifier("NuShoppingListClient_${clientIdSuffix}_${UUID.randomUUID()}")
                .serverHost("192.168.1.101")
                .serverPort(31883)
                .addConnectedListener { isConnected = true }
                .addDisconnectedListener { isConnected = false }
                .buildAsync()
        Log.i(tag, "created mqttClient")
    }


    override fun connect(): MqttServiceAdapter {
        Log.i(tag, "start connect")
        mqttClient.connectWith()
            .simpleAuth()
            .username("homeassistant")
            .password("password".encodeToByteArray())
            .applySimpleAuth()
            .send()
            .whenComplete { ack: Mqtt5ConnAck, error: Throwable ->
                isConnected = true
                Log.i(tag, "connected with error: $error")

            }
        Log.i(tag, "done connect")
        return this
    }

    fun disconnect() {
        if (!isConnected) {
            return
        }
        mqttClient.disconnect()
            .whenComplete { _: Void, _: Throwable -> isConnected = false }
    }

    fun subscribe(topic: String) {
        println("subscribe to topic $topic")
        mqttClient.subscribeWith()
            .topicFilter(topic)
            .qos(MqttQos.AT_LEAST_ONCE)
            .noLocal(true)
            .callback(messageHandler)
            .send()
    }

    fun unsubscribe(topic: String) {
        mqttClient.unsubscribeWith()
            .topicFilter(topic)
            .send()
    }

    override fun publish(messageObject: ShoppingMessage) {
        val topic = CONSTANTS.messagesWithTopic[messageObject::class]
        require(topic != null) { "Could not find topic for $messageObject" }
        if (!isConnected) {
            println("xxx\tCannot send $messageObject to $topic. Client is not connected")
        }
        println("==>\t$topic:\t $messageObject")
        mqttClient.publishWith()
            .topic(topic)
            .payload(ConfiguredObjectMapper().writeValueAsBytes(messageObject))
            .qos(MqttQos.EXACTLY_ONCE)
            .send()
    }

    fun setHandler(messageHandler: MessageHandler) {
        this.messageHandler = messageHandler
    }
}
