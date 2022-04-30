package de.moyapro.nushppinglist.constants

import de.moyapro.nushppinglist.constants.CONSTANTS.DISABLED
import de.moyapro.nushppinglist.constants.CONSTANTS.ENABLED

enum class MainView(val text: String, val enabled: Boolean = true) {
    EINKAUFSLISTE("Einkaufsliste"),
    CART("Listen"),
    SETTINGS("⚙"),
    SUBSCRIPTION_TEST("DateUpdater", ENABLED),
    MQTT_Test("MQTT", DISABLED),
    REZEPTE("Rezepte", DISABLED),
    COLOR_TEST("Color", DISABLED),
    ANIMATION_TEST("Animation", DISABLED),
    ;


}
