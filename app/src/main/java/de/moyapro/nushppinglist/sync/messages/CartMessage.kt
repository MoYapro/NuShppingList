package de.moyapro.nushppinglist.sync.messages

import de.moyapro.nushppinglist.constants.CONSTANTS.MQTT_TOPIC_CART
import de.moyapro.nushppinglist.db.ids.CartId
import de.moyapro.nushppinglist.db.model.CartItemProperties

/**
 * Send an update of the items in the cart
 */
data class CartMessage(
    val cartItemPropertiesList: List<CartItemProperties> = listOf(),
    val cartId: CartId? = null,
) : ShoppingMessage {
    constructor(vararg cartItemProperties: CartItemProperties, cartId: CartId) : this(
        cartItemPropertiesList = cartItemProperties.toList(),
        cartId = cartId
    )

    constructor(): this(listOf(), CartId())

    override fun getTopic(): String = "$MQTT_TOPIC_CART/${cartId?.id}"


}
