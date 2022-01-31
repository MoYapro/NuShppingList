package de.moyapro.nushppinglist.sync.messages

import com.fasterxml.jackson.annotation.JsonIgnore

sealed interface ShoppingMessage {

    @JsonIgnore
    fun getTopic() : String
}
