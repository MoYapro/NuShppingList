package de.moyapro.nushppinglist.sync.messages

import com.fasterxml.jackson.annotation.JsonCreator
import de.moyapro.nushppinglist.db.ids.ItemId
import java.util.*

data class RequestItemMessage(val itemId: ItemId) : ShoppingMessage {
    companion object {
        @JsonCreator
        @JvmStatic
        fun create(itemId: UUID) = RequestItemMessage(ItemId(itemId))
    }
}
