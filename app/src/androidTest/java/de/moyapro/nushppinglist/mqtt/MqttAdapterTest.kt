package de.moyapro.nushppinglist.mqtt

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.kotest.matchers.shouldBe
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MqttAdapterTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()


    private lateinit var sut: MqttAdapter

    @Before
    fun setup() {

        sut = MqttAdapter(context)
    }

    @Test(timeout = 20_000)
    fun connect() {
        var isConnected = false

        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.serverURIs = arrayOf("tcp://192.168.1.101:31883")
        mqttConnectOptions.userName = "homeassistant"
        mqttConnectOptions.password = "password".toCharArray()
        sut.connect(mqttConnectOptions) { isConnected = true }
        while (!isConnected) {
            Thread.sleep(100)
        }
//        sut.publish("sometopic", "hello android world ${System.currentTimeMillis()}")
        isConnected shouldBe true
    }



}
