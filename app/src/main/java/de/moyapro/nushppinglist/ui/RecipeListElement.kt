package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.moyapro.nushppinglist.constants.SWITCHES
import de.moyapro.nushppinglist.db.model.Recipe
import de.moyapro.nushppinglist.db.model.RecipeProperties
import de.moyapro.nushppinglist.ui.util.RecipePropertiesProvider
import de.moyapro.nushppinglist.ui.util.RecipeProvider


@Composable
@Preview
fun RecipeListElement(
    @PreviewParameter(RecipeProvider::class)
    recipe: Recipe,
    editMode: Boolean = false,
) {
    var isEdited: Boolean by remember { mutableStateOf(editMode) }

    Column(Modifier.background(color = Color.White)) {
        if (SWITCHES.DEBUG) {
            Text(recipe.recipeId.toString())
        }

        if (isEdited) {
            EditView(recipe) { isEdited = false }
        } else {
            JustView(recipe.recipeProperties) { isEdited = true }
        }
    }
}

@Composable
fun EditView(recipe: Recipe, endEditAction: () -> Unit) {
    Text(
        recipe.recipeProperties.title,
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dp(4F))
            .clickable(
                onClick = endEditAction
            )
    )

    recipe.recipeItems.forEach {
        Column() {
            Row {
                Text(amountText(it), modifier = Modifier.width(80.dp))
                Text(it.item.name)
            }
        }
    }
}

@Composable
@Preview
fun JustView(
    @PreviewParameter(RecipePropertiesProvider::class)
    recipeProperties: RecipeProperties,
    beginEditMode: () -> Unit = {},
) {
    Row(
        Modifier
            .background(color = Color.Red)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            recipeProperties.title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dp(4F))
                .clickable(onClick = beginEditMode)
        )
    }
}

