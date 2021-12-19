package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import de.moyapro.nushppinglist.ui.MainView.*
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.model.RecipeViewModel

@Composable
fun AppView(
    cartViewModel: CartViewModel,
    recipeViewModel: RecipeViewModel,
    selectedView: MainView = EINKAUFSLISTE,
) {
    var displayedView by remember { mutableStateOf(selectedView) }
    Column(Modifier.background(color = Color.Magenta)) {

        ViewSelector(displayedView) { newValue -> displayedView = newValue }

        when (displayedView) {
            EINKAUFSLISTE -> CartView(cartViewModel)
            DINGE -> ItemView(cartViewModel)
            REZEPTE -> RecipeListView(recipeViewModel, cartViewModel)
        }
    }
}

