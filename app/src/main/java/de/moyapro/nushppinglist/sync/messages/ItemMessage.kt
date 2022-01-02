package de.moyapro.nushppinglist.sync.messages

import de.moyapro.nushppinglist.db.model.Item

data class ItemMessage(val item: Item) : ShoppingMessage
