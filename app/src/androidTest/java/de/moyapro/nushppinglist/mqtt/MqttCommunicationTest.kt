package de.moyapro.nushppinglist.mqtt

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.moyapro.nushppinglist.util.waitFor
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MqttCommunicationTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    companion object {
        val mqttConnectOptions = MqttConnectOptions()
        val topic = "nuShoppingList/testtopic"

        init {
            mqttConnectOptions.serverURIs = arrayOf("tcp://192.168.1.101:31883")
            mqttConnectOptions.userName = "homeassistant"
            mqttConnectOptions.password = "password".toCharArray()
            mqttConnectOptions.isCleanSession = false

        }
    }

    private lateinit var serviceAdapterAlice: MqttServiceAdapter
    private lateinit var serviceAdapterBob: MqttServiceAdapter

    @Before
    fun setup() {
        serviceAdapterAlice =
            MqttServiceAdapter.Builder
                .createMqttAdapter(mqttConnectOptions, "alice")
                .connect()
        serviceAdapterBob =
            MqttServiceAdapter.Builder
                .createMqttServiceAdapter(context, mqttConnectOptions, "bob")
                .connect()

        while (!serviceAdapterAlice.isConnected() || !serviceAdapterBob.isConnected()) {
            Thread.sleep(100)
        }
    }

    @After
    fun tearDown() {
        serviceAdapterAlice.disconnect()
        serviceAdapterBob.disconnect()
    }

    @Test(timeout = 10_000)
    fun clientsAreConnected() {
        serviceAdapterAlice.isConnected() shouldBe true
        serviceAdapterBob.isConnected() shouldBe true
    }

    @Test(timeout = 10_000)
    fun communication() {
        var receivedMessage: IMqttToken? = null
        serviceAdapterBob.subscribe(topic) { receivedMessage = it }
        serviceAdapterAlice.publish(topic, "Hello Bob")
        waitFor { null != receivedMessage }
        receivedMessage shouldNotBe null
    }
}
