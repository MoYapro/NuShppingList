package de.moyapro.nushppinglist.sync.messages

import de.moyapro.nushppinglist.db.model.Item

data class ItemMessage(val items: List<Item>) : ShoppingMessage {
    constructor(vararg item: Item) : this(item.toList())
}
