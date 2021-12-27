package de.moyapro.nushppinglist.ui

import android.content.SharedPreferences
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.constants.SWITCHES
import de.moyapro.nushppinglist.ui.MainView.*
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.model.RecipeViewModel
import de.moyapro.nushppinglist.ui.test.AnimationTest
import de.moyapro.nushppinglist.ui.test.ColorTest

@Composable
fun AppView(
    preferences: SharedPreferences,
    cartViewModel: CartViewModel,
    recipeViewModel: RecipeViewModel,
    selectedView: MainView = SWITCHES.INITIAL_SCREEN,
) {

    var state by remember { mutableStateOf(selectedView.ordinal) }
    var displayedView by remember { mutableStateOf(selectedView) }

    Column(Modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = state) {
            values()
                .filter { it.enabled || SWITCHES.SHOW_ALL_SCREENS}
                .forEachIndexed { index, mainView ->
                    Tab(
                        text = { Text(mainView.text) },
                        selected = state == index,
                        onClick = {
                            state = index
                            displayedView = mainView
                        }
                    )
                }
        }
        when (displayedView) {
            EINKAUFSLISTE -> CartView(cartViewModel)
            DINGE -> ItemList(cartViewModel)
            REZEPTE -> RecipeListView(recipeViewModel, cartViewModel)
            COLOR_TEST -> ColorTest()
            ANIMATION_TEST -> AnimationTest()
            SETTINGS -> SettingsView(preferences)
        }
    }
}

enum class MainView(val text: String, val enabled: Boolean = true) {
    EINKAUFSLISTE("Einkaufsliste"),
    DINGE("Dinge"),
    SETTINGS("⚙"),
    REZEPTE("Rezepte", CONSTANTS.DISABLED),
    COLOR_TEST("Color", CONSTANTS.DISABLED),
    ANIMATION_TEST("Animation", CONSTANTS.DISABLED),
    ;


}


