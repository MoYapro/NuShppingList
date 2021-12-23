package de.moyapro.nushppinglist.constants

import androidx.compose.ui.graphics.Color

enum class KATEGORY(val displayName: String, val color: Color) {

    Obst("Obst", Color.Yellow),
    GEMUESE("Gemüse", Color.Green),
    FLEISCH("Fleisch", Color.Red),
    GETRAENKE("Getränke", Color.DarkGray),
    BROTBELAG("Brotbelag", Color.Blue),
    SUESSIGKEITEN("Süßigkeiten", Color.Magenta),
    SPIELZEUG("Spielzeug", Color.LightGray),
    GESUNGHEIT("Gesundheit", Color.Cyan),
    SCHULSACHEN("Schulsachen", Color.Black),
    REINIGUNG("Reinigung", Color.White),
    SONSTIGES("Sonstiges", Color.Transparent),

}
