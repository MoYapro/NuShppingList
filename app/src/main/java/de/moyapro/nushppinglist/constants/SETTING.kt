package de.moyapro.nushppinglist.constants

enum class SETTING(val label: String, val description: String = "") {
    CLEAR_AFTER_ADD("Eingabe löschen nach Hinzufügen",
        "Die Eingabe wird nach dem Hinzufügen eines neuen Eintrags gelöscht"),
    SYNC_ENABLED("Geteilte Einkaufsliste",
        "Die Einkaufsliste kann mit jemand anderes geteilt werden. Es muss auf allen Geräten mit denen die Liste geteilt werden soll die gleichen Servereinstellungen eingetragen werden"),
    SYNC_MQTT_SERVER_BASE_TOPIC("Name der Liste", "Name der Liste bzw das Topic, dass für die Kommunikation verwendet werden soll"),
    SYNC_MQTT_SERVER_HOSTNAME("MQTT Server Hostname", "Die Adresse des Servers, den du verwenden möchtest z.B. myinstance.s2.eu.hivemq.cloud:8883"),
    SYNC_MQTT_SERVER_USER("Benutzername", "Der Benutzername mit dem du dich an dem Server einloggen möchtest z.B. nuShoppingListUser"),
    SYNC_MQTT_SERVER_PASSWORD("Passwort", "Das Passwort mit dem du dich an dem Server einloggen möchtest z.B. *********"),
    SYNC_MQTT_SERVER_TLS("Server unterstützt TLS", "TLS sorgt für eine verschlüsselte Übertragung deiner Daten. Nur wenn du Probleme mit der Verbindung hast kannst du versuchen das auszuschalten."),
}
