package de.moyapro.nushppinglist.util.test

import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.messages.ShoppingMessage
import de.moyapro.nushppinglist.util.addOrAppend

object MockPublisher : Publisher {

    val messages: MutableMap<String, MutableList<ShoppingMessage>> = mutableMapOf()

    override fun publish(messageObject: ShoppingMessage) {
        messages.addOrAppend(messageObject.getTopic(), messageObject)
    }

    override fun connect(): Publisher {
        return this
    }

    fun reset() = messages.clear()
}
