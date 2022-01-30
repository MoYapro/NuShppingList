package de.moyapro.nushppinglist.sync.messages

import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.db.model.Item

data class ItemMessage(val items: List<Item>) : ShoppingMessage {
    constructor(vararg item: Item) : this(item.toList())

    override fun getTopic(): String = CONSTANTS.MQTT_TOPIC_ITEM
}
