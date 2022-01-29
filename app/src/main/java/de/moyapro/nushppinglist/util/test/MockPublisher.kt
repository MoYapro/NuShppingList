package de.moyapro.nushppinglist.util.test

import de.moyapro.nushppinglist.serialization.ConfiguredObjectMapper
import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.messages.ShoppingMessage

class MockPublisher(private val topic: String) : Publisher {

    val messages: MutableMap<String, String> = mutableMapOf()

    override fun publish(messageObject: ShoppingMessage) {
        messages[topic] = ConfiguredObjectMapper().writeValueAsString(messageObject)
    }

    override fun connect(): Publisher {
        return this
    }

    fun reset() = messages.clear()
}
