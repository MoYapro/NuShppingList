package de.moyapro.nushppinglist.constants

enum class MainView(val text: String, val enabled: Boolean = true) {
    EINKAUFSLISTE("Einkaufsliste"),
    DINGE("Dinge"),
    SETTINGS("⚙"),
    MQTT_Test("MQTT"),
    REZEPTE("Rezepte", CONSTANTS.DISABLED),
    COLOR_TEST("Color", CONSTANTS.DISABLED),
    ANIMATION_TEST("Animation", CONSTANTS.DISABLED),
    ;


}
