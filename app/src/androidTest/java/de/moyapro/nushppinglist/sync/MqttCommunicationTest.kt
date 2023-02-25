package de.moyapro.nushppinglist.sync

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.sync.messages.RequestItemMessage
import de.moyapro.nushppinglist.ui.util.waitFor
import io.kotest.matchers.shouldBe
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MqttCommunicationTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    companion object {
        const val topic = "nuShoppingList/testtopic"
    }

    @Before
    fun setup() {

    }

    @Test//(timeout = 10_000)
    fun communication() {
        var messageArrived = false
        val serviceAdapterAlice = MqttServiceAdapter("MqttAdapterTest_Alice") { messageArrived = true }
        var messageReceived = false
        val serviceAdapterBob = MqttServiceAdapter("MqttAdapterTest_Bob") { messageArrived = true }

        waitFor { serviceAdapterAlice.isConnected() }
        waitFor { serviceAdapterBob.isConnected() }

        serviceAdapterBob.subscribe()
        serviceAdapterAlice.publish(RequestItemMessage(ItemId()))

        waitFor { messageReceived }
        messageReceived shouldBe true
        messageArrived shouldBe true

    }
}
