package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import de.moyapro.nushppinglist.db.model.Recipe
import de.moyapro.nushppinglist.ui.model.RecipeViewModel


@Composable
fun RecipeListView(viewModel: RecipeViewModel) {
    val recipes: List<Recipe> by viewModel.allRecipes.collectAsState(listOf())

    Column(Modifier.background(color = Color.Green)) {
        recipes.forEach { recipe ->
            RecipeListElement(recipe)
        }
    }
}
