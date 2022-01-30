package de.moyapro.nushppinglist.sync.messages

import com.fasterxml.jackson.annotation.JsonCreator
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.db.ids.ItemId
import java.util.*

data class RequestItemMessage(val itemIds: List<ItemId>) : ShoppingMessage {

    constructor(itemId: ItemId) : this(listOf(itemId))

    companion object {
        @JsonCreator
        @JvmStatic
        fun create(itemIds: List<UUID>) = RequestItemMessage(itemIds.map(::ItemId))
    }

    override fun getTopic(): String = CONSTANTS.MQTT_TOPIC_ITEM_REQUEST

}
