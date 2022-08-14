package de.moyapro.nushppinglist.constants

enum class MainView(val text: String, val enabled: Boolean = true) {
    EINKAUFSLISTE("Einkaufsliste"),
    CART("Listen"),
    MQTT_Test("MQTT", CONSTANTS.ENABLED),
    SETTINGS("⚙"),
    DINGE("Dinge", CONSTANTS.DISABLED),
    REZEPTE("Rezepte", CONSTANTS.DISABLED),
    COLOR_TEST("Color", CONSTANTS.DISABLED),
    ANIMATION_TEST("Animation", CONSTANTS.DISABLED),
    ;


}
