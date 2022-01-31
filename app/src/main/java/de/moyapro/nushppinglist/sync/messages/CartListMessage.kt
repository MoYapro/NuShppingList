package de.moyapro.nushppinglist.sync.messages

import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.db.model.Cart

data class CartListMessage(val carts: List<Cart>) : ShoppingMessage {

    constructor(): this(emptyList())

    override fun getTopic(): String = CONSTANTS.MQTT_TOPIC_CARTLIST

}
