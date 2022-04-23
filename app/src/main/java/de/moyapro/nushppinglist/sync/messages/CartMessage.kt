package de.moyapro.nushppinglist.sync.messages

import de.moyapro.nushppinglist.constants.CONSTANTS.MQTT_TOPIC_CART
import de.moyapro.nushppinglist.db.ids.CartId
import de.moyapro.nushppinglist.db.model.CartItemProperties
import de.moyapro.nushppinglist.db.model.Item

/**
 * Send an update of the items in the cart
 */
data class CartMessage(
    val cartItemPropertiesList: List<CartItemProperties> = emptyList(),
    val itemList: List<Item> = emptyList(),
    val cartId: CartId? = null,
) : ShoppingMessage {
    constructor(vararg cartItemProperties: CartItemProperties, cartId: CartId) : this(
        cartItemPropertiesList = cartItemProperties.toList(),
        cartId = cartId
    )

    constructor() : this(emptyList(), emptyList(), CartId())

    constructor(cartItemPropertiesList: List<CartItemProperties>, cartId: CartId?) : this(
        cartItemPropertiesList,
        emptyList(),
        cartId)

    override fun getTopic(): String = "$MQTT_TOPIC_CART/${cartId?.id}"


}
