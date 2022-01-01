package de.moyapro.nushppinglist.sync

interface Publisher {
    fun publish(topic: String, messageObject: Any)
}
