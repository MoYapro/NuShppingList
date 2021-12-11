package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.moyapro.nushppinglist.constants.SWITCHES
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.db.model.Recipe
import de.moyapro.nushppinglist.db.model.RecipeItem
import de.moyapro.nushppinglist.db.model.RecipeProperties
import de.moyapro.nushppinglist.ui.util.RecipePropertiesProvider
import de.moyapro.nushppinglist.ui.util.RecipeProvider
import de.moyapro.nushppinglist.ui.util.ToggleList


@Composable
@Preview
fun RecipeListElement(
    @PreviewParameter(RecipeProvider::class)
    recipe: Recipe,
    editMode: Boolean = false,
    addSelectedItemsToCartAction: (List<RecipeItem>) -> Unit = {},
) {
    var isEdited: Boolean by remember { mutableStateOf(editMode) }

    Column(Modifier.background(color = Color.White)) {
        if (SWITCHES.DEBUG) {
            Text(recipe.recipeId.toString())
        }

        if (isEdited) {
            EditView(
                recipe,
                endEditAction = { isEdited = false },
                addSelectedItemsToCartAction = addSelectedItemsToCartAction
            )
        } else {
            JustView(recipe.recipeProperties) { isEdited = true }
        }
    }
}

@Composable
fun EditView(
    recipe: Recipe,
    endEditAction: () -> Unit,
    addNewItemToRecipeAction: () -> Unit = {},
    addSelectedItemsToCartAction: (List<RecipeItem>) -> Unit,
) {
    var toBeAdded: ToggleList<ItemId, Color> by remember {
        mutableStateOf(
            ToggleList(
                onValue = Color.Green,
                offValue = Color.Transparent,
                recipe.recipeItems.map { it.item.itemId },
                isActive = false
            ))
    }
    val startAdding: () -> Unit = { toBeAdded = toBeAdded.toggleActive() }
    val doAdding: () -> Unit =
        {
            toBeAdded = toBeAdded.toggleActive()
            addSelectedItemsToCartAction(recipe.recipeItems.filter { toBeAdded.contains(it.item.itemId) })
        }

    Text(
        recipe.recipeProperties.title,
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dp(4F))
            .clickable(
                onClick = endEditAction
            )
    )
    Button(onClick = { addNewItemToRecipeAction() }) {
        Text("+")
    }
    Button(onClick = if (!toBeAdded.isActive) startAdding else doAdding) {
        Text(text = if (toBeAdded.isActive) "🛒!" else "🛒")
    }

    recipe.recipeItems.forEach { recipeItem ->
        val itemId = recipeItem.item.itemId
        Column {
            Row(modifier = Modifier
                .background(toBeAdded.getValue(itemId) ?: Color.Magenta)
                .clickable { toBeAdded = toBeAdded.toggle(itemId) })
            {
                Text(amountText(recipeItem), modifier = Modifier.width(80.dp))
                Text(recipeItem.item.name)
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

