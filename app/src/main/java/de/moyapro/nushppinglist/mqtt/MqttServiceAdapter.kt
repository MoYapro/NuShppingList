package de.moyapro.nushppinglist.mqtt


import android.content.Context
import android.util.Log
import de.moyapro.nushppinglist.constants.SWITCHES
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.nio.charset.StandardCharsets


class MqttServiceAdapter private constructor(
    private val mqttClient: IMqttAsyncClient
) {
    private val tag = "MqttClient"

    companion object Builder {
        fun createMqttServiceAdapter(
            context: Context,
            mqttConnectOptions: MqttConnectOptions,
            clientIdSuffix: String = "",
        )= MqttServiceAdapter(
        MqttAndroidClient(
            context,
            mqttConnectOptions.serverURIs[0],
            "NuShoppingListServiceClient_$clientIdSuffix"
        )
        )
        fun createMqttAdapter(
            mqttConnectOptions: MqttConnectOptions,
            clientIdSuffix: String = "",
        )= MqttServiceAdapter(
            MqttAsyncClient(
                mqttConnectOptions.serverURIs[0],
                "NuShoppingListClient_$clientIdSuffix",
                MemoryPersistence()
            )
        )
        }

    private var isConnected = false
    fun isConnected() = isConnected



    init {
        if (SWITCHES.DEBUG) {
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
                }

                override fun deliveryComplete(token: IMqttDeliveryToken) {
                    Log.i(tag, "deliveryComplete: $token")
                }
            })
        }
    }

    fun connect(successAction: (IMqttToken?) -> Unit = {}): MqttServiceAdapter {
        mqttClient.connect(MqttConnectOptions(), null, MqttActionListener { mqttToken ->
            isConnected = true
            successAction(mqttToken)
        })
        return this
    }

    fun disconnect() {
        if (!isConnected) {
            return
        }
        mqttClient.disconnect(null, MqttActionListener { isConnected = false })
    }

    fun subscribe(topic: String, successAction: (IMqttToken?) -> Unit) {
        mqttClient.subscribe(topic, 0, null, MqttActionListener(successAction))
    }

    fun unsubscribe(topic: String) {
        mqttClient.unsubscribe(topic, null, MqttActionListener())
    }

    fun publish(topic: String, message: String, successAction: (IMqttToken?) -> Unit) {
        mqttClient.publish(topic,
            message.toByteArray(StandardCharsets.UTF_8),
            0,
            false,
            null,
            MqttActionListener(successAction))
    }
}
