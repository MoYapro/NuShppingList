package de.moyapro.nushppinglist.constants

enum class SETTING(val label: String, val description: String = "") {
    CLEAR_AFTER_ADD("Eingabe löschen nach Hinzufügen",
        "Die Eingabe wird nach dem Hinzufügen eines neuen Eintrags gelöscht"),
    CART_IDENT("Kennzeichnung der Einkaufsliste",
        "Diese Kennzeichnung wird benötigt, wenn die Einkaufsliste geteilt werden soll. Dazu diese Kennzeichnung teilen oder eine erhaltene Kennzeichnung hier einfügen")
}
