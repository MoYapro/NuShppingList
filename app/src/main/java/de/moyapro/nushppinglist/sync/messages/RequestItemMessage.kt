package de.moyapro.nushppinglist.sync.messages

import com.fasterxml.jackson.annotation.JsonCreator
import de.moyapro.nushppinglist.db.ids.ItemId
import java.util.*

data class RequestItemMessage(@get:JvmName("getItemId")val itemId: ItemId) {
    companion object {
        @JsonCreator
        @JvmStatic
        fun create(itemId: UUID) = RequestItemMessage(ItemId(itemId))
    }
}
