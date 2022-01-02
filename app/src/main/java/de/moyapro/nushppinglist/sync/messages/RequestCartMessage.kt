package de.moyapro.nushppinglist.sync.messages

import com.fasterxml.jackson.annotation.JsonCreator

data class RequestCartMessage(
    val message: String = "Can I get your cart, please?",
) : ShoppingMessage {
    companion object {
        @JsonCreator
        @JvmStatic
        fun create(message: String) = RequestCartMessage(message)
    }
}
