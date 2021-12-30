package de.moyapro.nushppinglist.mqtt


import android.content.Context
import android.util.Log
import de.moyapro.nushppinglist.constants.SWITCHES
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.nio.charset.StandardCharsets

class MqttAdapter(
    val context: Context,
    val mqttConnectOptions: MqttConnectOptions,
) {


    companion object {
        const val TAG = "MqttAdapter"
    }

    private val clientId: String = "NuShoppingListClient"
    private val host: String = mqttConnectOptions.serverURIs[0]
    private val username: String = mqttConnectOptions.userName
    private val password: CharArray = mqttConnectOptions.password

    private var isConnected = false

    private val mqttClient by lazy {
        MqttAndroidClient(
            context,
            host,
            clientId,
        )
    }

    init {
        if (SWITCHES.DEBUG) {
            mqttClient.setCallback(object : MqttCallbackExtended {
                override fun connectComplete(reconnect: Boolean, serverURI: String) {
                    isConnected = true
                    Log.i(TAG, "connectComplete with serverURI: $serverURI")
                }

                override fun connectionLost(cause: Throwable?) {
                    isConnected = false
                    Log.w(TAG, cause?.message ?: "Connection Lost without error message")
                    cause?.printStackTrace()
                }

                override fun messageArrived(topic: String, message: MqttMessage) {
                    Log.i(TAG, "messageArraived: $message")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken) {
                    Log.i(TAG, "deliveryComplete: $token")
                }
            })
        }
    }

    val disconnectedBufferOptions = DisconnectedBufferOptions().apply {
        isBufferEnabled = DisconnectedBufferOptions.DISCONNECTED_BUFFER_ENABLED_DEFAULT
        isPersistBuffer = DisconnectedBufferOptions.PERSIST_DISCONNECTED_BUFFER_DEFAULT
        bufferSize = DisconnectedBufferOptions.DISCONNECTED_BUFFER_SIZE_DEFAULT
        isDeleteOldestMessages = DisconnectedBufferOptions.DELETE_OLDEST_MESSAGES_DEFAULT
    }

    fun connect(successAction: (IMqttToken?) -> Unit = {}) {
        mqttClient.connect(mqttConnectOptions, null, MqttActionListener { mqttToken ->
            isConnected = true
            successAction(mqttToken)
        })
    }

    fun disconnect() {
        if (!isConnected) {
            return
        }
        mqttClient.disconnect(null, MqttActionListener { isConnected = false })
    }

    // Subscribe to topic
    fun subscribe(topic: String, successAction: (IMqttToken?) -> Unit) {
        mqttClient.subscribe(topic, 0, null, MqttActionListener(successAction))
    }

    // Unsubscribe the topic
    fun unsubscribe(topic: String) {

        mqttClient.unsubscribe(topic, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            }

        })

    }

    fun publish(topic: String, message: String, successAction: (IMqttToken?) -> Unit = {}) {
        mqttClient.publish(topic,
            message.toByteArray(StandardCharsets.UTF_8),
            0,
            false,
            null,
            MqttActionListener(successAction))
    }
}
