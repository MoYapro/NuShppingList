package de.moyapro.nushppinglist.mqtt


import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.nio.charset.StandardCharsets

class MqttAdapter(val context: Context) {

    private val TAG = "MqttAdapter"

    private val clientId: String = ""
    private val host: String = ""
    private val topic: String = ""
    private val username: String = ""
    private val password: String = ""

    private var isConnected = false

    private val mqttClient by lazy {
        MqttAndroidClient(
            context,
            host,
            clientId,
        )
    }

    init {
        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String) {
                Log.i(TAG, "connectComplete with serverURI: $serverURI")
            }

            override fun connectionLost(cause: Throwable) {
                cause.printStackTrace()
                throw cause
            }

            override fun messageArrived(topic: String, message: MqttMessage) {
                Log.i(TAG, "messageArraived: $message")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                Log.i(TAG, "deliveryComplete: $token")
            }
        })
    }

    val disconnectedBufferOptions = DisconnectedBufferOptions().apply {
        isBufferEnabled = DisconnectedBufferOptions.DISCONNECTED_BUFFER_ENABLED_DEFAULT
        isPersistBuffer = DisconnectedBufferOptions.PERSIST_DISCONNECTED_BUFFER_DEFAULT
        bufferSize = DisconnectedBufferOptions.DISCONNECTED_BUFFER_SIZE_DEFAULT
        isDeleteOldestMessages = DisconnectedBufferOptions.DELETE_OLDEST_MESSAGES_DEFAULT
    }

    fun connect(mqttConnectOptions: MqttConnectOptions, successAction: (IMqttToken?) -> Unit = {}) {
        mqttClient.connect(mqttConnectOptions, null, MqttActionListener { mqttToken ->
            isConnected = true
            println("messageId: ${mqttToken?.messageId}")
            println("exception: ${mqttToken?.exception}")
            println("response: ${mqttToken?.response}")
            successAction(mqttToken)
        })
    }

    fun disconnect() {
        if (!isConnected) return

        mqttClient.disconnect(null, object : IMqttActionListener {
            /**
             * This method is invoked when an action has completed successfully.
             * @param asyncActionToken associated with the action that has completed
             */
            override fun onSuccess(asyncActionToken: IMqttToken?) {
            }

            /**
             * This method is invoked when an action fails.
             * If a client is disconnected while an action is in progress
             * onFailure will be called. For connections
             * that use cleanSession set to false, any QoS 1 and 2 messages that
             * are in the process of being delivered will be delivered to the requested
             * quality of service next time the client connects.
             * @param asyncActionToken associated with the action that has failed
             */
            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            }

        })
    }

    // Subscribe to topic
    fun subscribe(topic: String) {
        mqttClient.subscribe(topic, 0, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                Log.i(TAG, "Subscription!")
            }

            override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                Log.i(TAG, "Subscription fail!")
            }
        })
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

    fun publish(topic: String, message: String) {
        mqttClient.publish(topic,
            message.toByteArray(StandardCharsets.UTF_8),
            0,
            false,
            null,
            object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.i(TAG, "Publish Success!")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.i(TAG, "Publish Failed!")
                }

            })
    }
}

private class MqttActionListener(
    val successAction: (IMqttToken?) -> Unit = {},
) : IMqttActionListener {

    override fun onSuccess(asyncActionToken: IMqttToken?) {
        successAction(asyncActionToken)
    }


    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
        println("Could not connect to mqtt server")
        println("token is: $asyncActionToken")
        exception?.printStackTrace()
        throw exception!!
    }

}
