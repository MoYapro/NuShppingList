package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import de.moyapro.nushppinglist.db.model.Recipe
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.model.RecipeViewModel


@Composable
fun RecipeListView(
    recipeViewModel: RecipeViewModel,
    cartViewModel: CartViewModel,
) {
    val recipes: List<Recipe> by recipeViewModel.allRecipes.collectAsState(listOf())

    Column(Modifier.background(color = Color.Green)) {
        recipes.forEach { recipe ->
            RecipeListElement(
                recipe = recipe,
                addSelectedItemsToCartAction = cartViewModel::addToCart,
                cartViewModel = cartViewModel
            )
        }
    }
}
