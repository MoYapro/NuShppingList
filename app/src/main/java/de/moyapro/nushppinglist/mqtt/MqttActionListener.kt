package de.moyapro.nushppinglist.mqtt

import android.util.Log
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken

class MqttActionListener(
    val successAction: (IMqttToken?) -> Unit = {},
) : IMqttActionListener {

    private val tag = "MqttListener"

    override fun onSuccess(asyncActionToken: IMqttToken?) {
        successAction(asyncActionToken)
    }


    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
        Log.w(tag, "Could not connect to mqtt server")
        Log.w(tag, "token is: $asyncActionToken")
        Log.w(tag, "exception is: $exception")
        exception?.printStackTrace()
        throw exception!!
    }

}
