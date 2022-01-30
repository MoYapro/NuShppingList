package de.moyapro.nushppinglist.sync.messages

sealed interface ShoppingMessage {

    fun getTopic() : String
}
