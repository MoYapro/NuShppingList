package de.moyapro.nushppinglist.sync


import android.util.Log
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import de.moyapro.nushppinglist.MainActivity
import de.moyapro.nushppinglist.serialization.ConfiguredObjectMapper
import de.moyapro.nushppinglist.settings.ConnectionSettings
import de.moyapro.nushppinglist.settings.SettingsConverter
import de.moyapro.nushppinglist.sync.messages.ShoppingMessage
import java.util.*


class MqttServiceAdapter(
    clientIdSuffix: String = "",
    private var messageHandler: (Mqtt5Publish) -> Unit = {},
) : Publisher {
    val clientIdentifier = "NuShoppingList_App_${clientIdSuffix}_${UUID.randomUUID()}"
    private val tag = MqttServiceAdapter::class.simpleName
    private val mqttClient: Mqtt5AsyncClient?
    private val connectionSettings: ConnectionSettings
    private var isConnected = false
    fun isConnected() = isConnected

    init {
        Log.d(tag, "init")
        connectionSettings = SettingsConverter.toConnectionSettings(MainActivity.preferences)
        mqttClient = if (
            connectionSettings.syncEnabled
            && connectionSettings != SettingsConverter.INVALID_CONNECTION_SETTINGS
        ) {
            val builder = MqttClient.builder()
                .useMqttVersion5()
                .identifier(clientIdentifier)
                .serverHost(connectionSettings.hostname)
                .serverPort(connectionSettings.port)
                .addConnectedListener { isConnected = true }
                .addDisconnectedListener {
                    Log.w(tag, "client $clientIdentifier disconnected from server: $it")
                    isConnected = false
                }
            if (connectionSettings.useTls) {
                builder.sslWithDefaultConfig()
            }
            builder.buildAsync()
        } else {
            null
        }
    }


    override fun connect(): MqttServiceAdapter {
        Log.d(tag, "connect to MQTT using $connectionSettings")
        mqttClient?.connectWith()
            ?.simpleAuth()
            ?.username(connectionSettings.username)
            ?.password(connectionSettings.password.encodeToByteArray())
            ?.applySimpleAuth()
            ?.send()
            ?.whenComplete { _: Mqtt5ConnAck, error: Throwable ->
                isConnected = true
                Log.d(tag, "connected with error: $error")

            }
        return this
    }

    fun disconnect() {
        if (!isConnected) {
            return
        }
        mqttClient?.disconnect()
            ?.whenComplete { _: Void, _: Throwable -> isConnected = false }
    }

    fun subscribe() {
        val topic = "${connectionSettings.topic}/#"
        Log.d(tag, "subscribe to topic $topic")
        mqttClient?.subscribeWith()
            ?.topicFilter(topic)
            ?.qos(MqttQos.AT_LEAST_ONCE)
            ?.noLocal(true)
            ?.callback(messageHandler)
            ?.send()
    }

    fun unsubscribe(topic: String) {
        mqttClient?.unsubscribeWith()
            ?.topicFilter(topic)
            ?.send()
    }

    override fun publish(messageObject: ShoppingMessage) {
        if (null == mqttClient) return
        val topic = connectionSettings.topic + "/" + messageObject.getTopic()
        if (!isConnected) {
            Log.d(tag, "xxx\tCannot send $messageObject to $topic. Client is not connected")
            return
        }
        Log.d(tag, "==>\t$topic:\t $messageObject")
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
