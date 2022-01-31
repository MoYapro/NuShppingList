package de.moyapro.nushppinglist.sync.messages

import de.moyapro.nushppinglist.constants.CONSTANTS
import java.util.*

data class RequestCartListMessage(val message: String) : ShoppingMessage {
    constructor(): this(UUID.randomUUID().toString())

    override fun getTopic(): String = CONSTANTS.MQTT_TOPIC_CARTLIST_REQUEST
}
