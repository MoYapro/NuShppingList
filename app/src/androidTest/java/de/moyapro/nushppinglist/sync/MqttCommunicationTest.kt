package de.moyapro.nushppinglist.sync

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.sync.messages.RequestItemMessage
import de.moyapro.nushppinglist.ui.util.waitFor
import io.kotest.matchers.shouldBe
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
        const val topic = "nuShoppingList/testtopic"

        init {
            mqttConnectOptions.serverURIs = arrayOf("tcp://192.168.1.101:31883")
            mqttConnectOptions.userName = "homeassistant"
            mqttConnectOptions.password = "password".toCharArray()
            mqttConnectOptions.isCleanSession = false

        }
    }

    private lateinit var serviceAdapterAlice: MqttServiceAdapter

    @Before
    fun setup() {
        serviceAdapterAlice =
            MqttServiceAdapter.Builder
                .createMqttAdapter(mqttConnectOptions, "alice")
                .connect()
        while (!serviceAdapterAlice.isConnected()) {
            Thread.sleep(100)
        }
    }

    @After
    fun tearDown() {
        serviceAdapterAlice.disconnect()
    }

    @Test(timeout = 10_000)
    fun communication() {
        var messageReceived = false
        val serviceAdapterBob =
            MqttServiceAdapter.Builder
                .createMqttServiceAdapter(context, mqttConnectOptions, "bob")
                { _ -> messageReceived = true }
                .connect()


        waitFor { serviceAdapterBob.isConnected() }

        serviceAdapterBob.subscribe(topic)
        serviceAdapterAlice.publish(RequestItemMessage(ItemId()))

        waitFor { messageReceived }
        messageReceived shouldBe true

    }
}
