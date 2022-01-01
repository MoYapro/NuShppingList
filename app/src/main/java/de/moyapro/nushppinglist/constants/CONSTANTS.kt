package de.moyapro.nushppinglist.constants

object CONSTANTS {

    const val PREFERENCES_FILE_NAME = "nuShoppingListPreferences"
    const val MQTT_TOPIC_BASE = "nuShoppingList"
    const val MQTT_TOPIC_ITEM_REQUEST = "$MQTT_TOPIC_BASE/item/request"
    const val MQTT_TOPIC_ITEM = "$MQTT_TOPIC_BASE/item"
    const val MQTT_TOPIC_CART_REQUEST = "$MQTT_TOPIC_BASE/cart/request" // we have only one cart at the moment
    const val MQTT_TOPIC_CART_UPDATE = "$MQTT_TOPIC_BASE/cart/update"
    const val MQTT_TOPIC_CART = "$MQTT_TOPIC_BASE/cart"


    const val MUTED_ALPHA = .7F

    const val CHECKED = true
    const val UNCHECKED = false
    const val CART_CHAR = "🛒"


    const val DISABLED: Boolean = false

}
