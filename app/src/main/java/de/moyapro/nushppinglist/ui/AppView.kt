package de.moyapro.nushppinglist.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import de.moyapro.nushppinglist.constants.MainView
import de.moyapro.nushppinglist.constants.MainView.values
import de.moyapro.nushppinglist.constants.SWITCHES
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.model.RecipeViewModel
import de.moyapro.nushppinglist.ui.test.AnimationTest
import de.moyapro.nushppinglist.ui.test.ColorTest

@Composable
fun AppView(
    cartViewModel: CartViewModel,
    recipeViewModel: RecipeViewModel,
    context: Context,
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
            MainView.EINKAUFSLISTE -> CartView(cartViewModel)
            MainView.DINGE -> ItemList(cartViewModel)
            MainView.CART -> CartList(cartViewModel)
            MainView.REZEPTE -> RecipeListView(recipeViewModel, cartViewModel)
            MainView.MQTT_Test -> MqttTestView(context)
            MainView.COLOR_TEST -> ColorTest()
            MainView.ANIMATION_TEST -> AnimationTest()
            MainView.SETTINGS -> SettingsView()
        }
    }
}


