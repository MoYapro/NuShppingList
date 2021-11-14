package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import de.moyapro.nushppinglist.db.model.Recipe
import de.moyapro.nushppinglist.ui.util.RecipeListProvider


@Composable
@Preview
fun RecipeListView(@PreviewParameter(RecipeListProvider::class) recipes: Iterable<Recipe>) {
    Column(Modifier.background(color = Color.Green)) {
        recipes.forEach { recipe ->
            RecipeListElement(recipe)
        }
    }
}
