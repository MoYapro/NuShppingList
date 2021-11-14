package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import de.moyapro.nushppinglist.ui.MainView.*
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.util.RecipeListProvider

@Composable
fun AppView(viewModel: CartViewModel, selectedView: MainView = REZEPTE) {
    var displayedView by remember { mutableStateOf(selectedView) }
    Column(Modifier.background(color = Color.Magenta)) {

        ViewSelector(displayedView) { newValue -> displayedView = newValue }

        when (displayedView) {
            EINKAUFSLISTE -> CartView(viewModel)
            DINGE -> ItemView(viewModel)
            REZEPTE -> RecipeListView(recipes = RecipeListProvider().values.flatten().toList())
        }
    }
}

