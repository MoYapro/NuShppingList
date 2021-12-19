package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import de.moyapro.nushppinglist.ui.MainView.*
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.model.RecipeViewModel

@Composable
fun AppView(
    cartViewModel: CartViewModel,
    recipeViewModel: RecipeViewModel,
    selectedView: MainView = EINKAUFSLISTE,
) {

    var state by remember { mutableStateOf(0) }
    var displayedView by remember { mutableStateOf(selectedView) }

    Column(Modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = state) {
            values().forEachIndexed { index, mainView ->
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
        }
    }
}


