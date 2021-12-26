package de.moyapro.nushppinglist.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Color(78, 5, 160),
    primaryVariant = Color(37, 1, 78),
    secondary = Color(122, 2, 156),
    secondaryVariant = Color(93, 1, 119),
    background = Color.Black,
    surface = Color(10,2,20),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
)

private val LightColorPalette = lightColors(
    primary = Color(78, 5, 160),
    primaryVariant = Color(37, 1, 78),
    secondary = Color(122, 2, 156),
    secondaryVariant = Color(93, 1, 119),
    background = Color.White,
    surface = Color(125, 25, 253, 40),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

@Composable
fun NuShppingListTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
