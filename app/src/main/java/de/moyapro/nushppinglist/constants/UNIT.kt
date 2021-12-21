package de.moyapro.nushppinglist.constants

enum class UNIT(val long: String, val short: String) {

    UNSPECIFIED("", ""),

    // weights
    GRAMM("Gramm", "g"),
    KILOGRAMM("Kilogramm", "kg"),

    //volume
    LITER("Liter", "l"),
    TEELOEFFEL("Teelöffel", "TL"),
    ESSLOEFFEL("Esslöffel", "EL"),

    // count
    DOSE("Dosen", " x "),
    STUECK("Stück", " x "),
    PACK("Packung", " x "),
    KISTE("Kiste", " x ")

}
