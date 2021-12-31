package de.moyapro.nushppinglist.mqtt

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.kotest.matchers.shouldBe
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class MqttServiceAdapterTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()


    private lateinit var sut: MqttServiceAdapter

    @Before
    fun setup() {
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.serverURIs = arrayOf("tcp://192.168.1.101:31883")
        mqttConnectOptions.userName = "homeassistant"
        mqttConnectOptions.password = "password".toCharArray()
        mqttConnectOptions.isCleanSession = false
        sut = MqttServiceAdapter.Builder.createMqttServiceAdapter(context, mqttConnectOptions)
    }

    @Test(timeout = 10_000)
    fun connectAndPublish() {
        val topic = "nuShoppingList/testtopic"
        var isConnected = false
        var sendSuccessfully = false
        var gotMessage = false

        sut.connect { isConnected = true }
        while (!isConnected) {
            Thread.sleep(100)
        }
        isConnected shouldBe true
        sut.subscribe(topic) { gotMessage = true }
        sut.publish(topic, "hello android world ${LocalDateTime.now()}") { sendSuccessfully = true }
        sut.disconnect()
        sendSuccessfully shouldBe true
        while (!gotMessage) {
            Thread.sleep(100) // wait a bit until message arrived
        }
        gotMessage shouldBe true
    }


}
