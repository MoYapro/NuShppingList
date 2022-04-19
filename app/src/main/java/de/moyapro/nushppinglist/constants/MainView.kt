package de.moyapro.nushppinglist.constants

enum class MainView(val text: String, val enabled: Boolean = true) {
    EINKAUFSLISTE("Einkaufsliste"),
    CART("Listen"),
    SETTINGS("⚙"),
    DINGE("Dinge", CONSTANTS.DISABLED),
    MQTT_Test("MQTT", CONSTANTS.DISABLED),
    REZEPTE("Rezepte", CONSTANTS.DISABLED),
    COLOR_TEST("Color", CONSTANTS.DISABLED),
    ANIMATION_TEST("Animation", CONSTANTS.DISABLED),
    ;


}
