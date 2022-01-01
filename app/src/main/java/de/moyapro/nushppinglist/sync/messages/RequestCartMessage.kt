package de.moyapro.nushppinglist.sync.messages

import com.fasterxml.jackson.annotation.JsonCreator

data class RequestCartMessage(val message: String) {
    companion object {
        @JsonCreator
        @JvmStatic
        fun create(message: String) = RequestCartMessage(message)
    }
}
