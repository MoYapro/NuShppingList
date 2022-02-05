package de.moyapro.nushppinglist.sync

import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.sync.messages.RequestItemMessage
import de.moyapro.nushppinglist.ui.util.waitFor
import io.kotest.matchers.shouldBe
import org.junit.Test


class MqttAdapterTest {
    @Test(timeout = 10_000)
    fun connection() {
        var messageArrived = false
        val adapterAlice = MqttServiceAdapter("MqttAdapterTest_Alice") { messageArrived = true }
        val adapterBob = MqttServiceAdapter("MqttAdapterTest_Bob") { messageArrived = true }
        adapterAlice.connect()
        adapterBob.connect()
        waitFor { adapterAlice.isConnected() && adapterBob.isConnected() }
        adapterBob.subscribe()
        adapterAlice.publish(RequestItemMessage(ItemId()))
        waitFor { messageArrived }
        messageArrived shouldBe true

    }
}
