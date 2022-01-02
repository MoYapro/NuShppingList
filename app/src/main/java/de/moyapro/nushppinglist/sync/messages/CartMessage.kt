package de.moyapro.nushppinglist.sync.messages

import de.moyapro.nushppinglist.db.model.CartItemProperties

data class CartMessage(val cartItemPropertiesList: List<CartItemProperties>) : ShoppingMessage
