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
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.constants.SWITCHES
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.db.model.CartItemProperties
import de.moyapro.nushppinglist.db.model.Recipe
import de.moyapro.nushppinglist.db.model.RecipeItem
import de.moyapro.nushppinglist.db.model.RecipeProperties
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.util.RecipePropertiesProvider
import de.moyapro.nushppinglist.ui.util.RecipeProvider
import de.moyapro.nushppinglist.ui.util.ToggleList


@Composable
fun RecipeListElement(
    @PreviewParameter(RecipeProvider::class)
    recipe: Recipe,
    editMode: Boolean = false,
    addSelectedItemsToCartAction: (List<RecipeItem>) -> Unit = {},
    cartViewModel: CartViewModel
) {
    var isEdited: Boolean by remember { mutableStateOf(editMode) }
    val cartItems: List<CartItemProperties> by cartViewModel.cartItems.collectAsState(listOf())


    Column(Modifier.background(color = Color.White)) {
        if (SWITCHES.DEBUG) {
            Text(recipe.recipeId.toString())
        }

        if (isEdited) {
            EditView(
                recipe,
                endEditAction = { isEdited = false },
                addSelectedItemsToCartAction = addSelectedItemsToCartAction,
            cartItems = cartItems
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
    cartItems: List<CartItemProperties>,
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
        Text(text = CONSTANTS.CART_CHAR + if (toBeAdded.isActive) "!" else "")
    }

    recipe.recipeItems.forEach { recipeItem ->
        val itemId = recipeItem.item.itemId
        val isInCart = cartItems.map { it.itemId }.contains(itemId)
        Column {
            Row(modifier = Modifier
                .background(toBeAdded.getValue(itemId) ?: Color.Magenta)
                .clickable { toBeAdded = toBeAdded.toggle(itemId) })
            {
                Text(amountText(recipeItem), modifier = Modifier.width(80.dp))
                Text(recipeItem.item.name)
                Text(" - $isInCart")
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

