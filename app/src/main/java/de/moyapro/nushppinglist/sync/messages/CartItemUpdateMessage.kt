package de.moyapro.nushppinglist.sync.messages

import de.moyapro.nushppinglist.db.model.CartItemProperties

data class CartItemUpdateMessage(val cartItemProperties: CartItemProperties)
