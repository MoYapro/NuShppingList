package de.moyapro.nushppinglist.sync

import de.moyapro.nushppinglist.sync.messages.ShoppingMessage

interface Publisher {
    fun publish(messageObject: ShoppingMessage)
}
