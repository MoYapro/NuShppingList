package de.moyapro.nushppinglist

import de.moyapro.nushppinglist.serialization.ConfiguredObjectMapper
import de.moyapro.nushppinglist.sync.Publisher

class MockPublisher(private val topic: String) : Publisher {

    val messages: MutableMap<String, String> = mutableMapOf()

    override fun publish(ignored: String, messageObject: Any) {
        messages[topic] = ConfiguredObjectMapper.writeValueAsString(messageObject)
    }

    fun reset() = messages.clear()
}
