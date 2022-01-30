package de.moyapro.nushppinglist.sync.messages

import com.fasterxml.jackson.annotation.JsonCreator
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.db.ids.CartId
import java.util.*

data class RequestCartMessage(val cartId: CartId?) : ShoppingMessage {
    companion object {
        @JsonCreator
        @JvmStatic
        fun create(message: String) = try {
            RequestCartMessage(CartId(UUID.fromString(message)))
        } catch (e: Exception) {
            RequestCartMessage(null)
        }
    }

    override fun getTopic(): String = CONSTANTS.MQTT_TOPIC_CART_REQUEST

}
