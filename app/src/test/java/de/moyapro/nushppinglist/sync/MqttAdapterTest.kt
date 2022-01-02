package de.moyapro.nushppinglist.sync

import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.sync.messages.RequestItemMessage
import de.moyapro.nushppinglist.ui.util.waitFor
import io.kotest.matchers.shouldBe
import org.junit.Test


class MqttAdapterTest {
    @Test(timeout = 100_000)
    fun connection() {
        var messageArrived = false
        val adapterAlice = MqttServiceAdapter("MqttAdapterTest_Alice") { messageArrived = true }
        val adapterBob = MqttServiceAdapter("MqttAdapterTest_Bob") { messageArrived = true }
        adapterAlice.connect()
        adapterBob.connect()
        Thread.sleep(1000)
        waitFor { adapterAlice.isConnected() && adapterBob.isConnected() }
        adapterBob.subscribe(CONSTANTS.messagesWithTopic[RequestItemMessage::class]!!)
        adapterAlice.publish(RequestItemMessage(ItemId()))

        Thread.sleep(1000)
        messageArrived shouldBe true

    }

    @Test
    fun raw() {
        var connected = false
        val client: Mqtt5AsyncClient = MqttClient.builder()
            .identifier("rawtest")
            .serverHost("192.168.1.101")
            .serverPort(31883)
            .useMqttVersion5()
            .executorConfig()
            .nettyThreads(1)
            .applyExecutorConfig()
            .addConnectedListener { connected = true }
            .addDisconnectedListener { connected = false }
            .automaticReconnectWithDefaultConfig()
            .buildAsync()
        client.connect().whenComplete { x, y -> println("connected: $x, $y") }

        Thread.sleep(500)
        val topic = "nuShoppingList/bar"
        client.subscribeWith()
            .topicFilter(topic)
            .qos(MqttQos.AT_LEAST_ONCE)
            .callback { println("got message: $it") }
            .send()
            .whenComplete { ack, error -> println("subscribed:  $ack, $error, $topic") }
        Thread.sleep(500)

        client.publish(
            Mqtt5Publish
                .builder()
                .topic(topic)
                .payload("hello".encodeToByteArray())
                .build()
        ).whenComplete { t, u -> "published message: $t, $u" }

        Thread.sleep(5000)
        connected shouldBe true
    }
}
