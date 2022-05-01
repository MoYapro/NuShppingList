package de.moyapro.nushppinglist.constants

import de.moyapro.nushppinglist.sync.messages.CartMessage
import de.moyapro.nushppinglist.sync.messages.ItemMessage
import de.moyapro.nushppinglist.sync.messages.RequestCartMessage
import de.moyapro.nushppinglist.sync.messages.RequestItemMessage
import kotlin.reflect.KClass

object CONSTANTS {

    const val PREFERENCES_FILE_NAME = "nuShoppingListPreferences"
    const val MQTT_TOPIC_ITEM_REQUEST = "item/request"
    const val MQTT_TOPIC_CARTLIST_REQUEST = "cartlist/request"
    const val MQTT_TOPIC_CARTLIST = "cartlist"
    const val MQTT_TOPIC_ITEM = "item"
    const val MQTT_TOPIC_CART_REQUEST = "cart/request"
    const val MQTT_TOPIC_CART = "cart"

    val messagesWithTopic: Map<KClass<*>, String> = mapOf(
        CartMessage::class to MQTT_TOPIC_CART,
        ItemMessage::class to MQTT_TOPIC_ITEM,
        RequestCartMessage::class to MQTT_TOPIC_CART_REQUEST,
        RequestItemMessage::class to MQTT_TOPIC_ITEM_REQUEST,
    )

    const val DEFAULT_CART_NAME = "Einkauf"

    const val MUTED_ALPHA = .7F

    const val CHECKED = true
    const val UNCHECKED = false
    const val CART_CHAR = "🛒"


    const val ENABLED: Boolean = true
    const val DISABLED: Boolean = false

}
