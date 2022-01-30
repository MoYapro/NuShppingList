package de.moyapro.nushppinglist.sync.messages

import de.moyapro.nushppinglist.constants.CONSTANTS.MQTT_TOPIC_CART
import de.moyapro.nushppinglist.db.ids.CartId
import de.moyapro.nushppinglist.db.model.CartItemProperties

data class CartMessage(
    val cartItemPropertiesList: List<CartItemProperties>,
    val cartId: CartId,
) : ShoppingMessage {
    constructor(vararg cartItemProperties: CartItemProperties, cartId: CartId) : this(
        cartItemPropertiesList = cartItemProperties.toList(),
        cartId = cartId
    )

    override fun getTopic(): String = "$MQTT_TOPIC_CART/${cartId.id}"


}
