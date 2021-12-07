package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.RecipeId
import de.moyapro.nushppinglist.ui.model.CartViewModel


@Composable
fun CartView(viewModel: CartViewModel) {
    val collectAsState: State<Map<RecipeId?, List<CartItem>>> =
        viewModel.allCartItemsGrouped.collectAsState(
            mapOf()
        )
    val cartItemProperties: Map<RecipeId?, List<CartItem>> by collectAsState
    val chooseAction: (String) -> Unit = viewModel::addToCart
    val autocompleteAction: (String) -> List<String> = { searchString ->
        viewModel.getAutocompleteItems(searchString)
    }
    Column(Modifier.background(color = Color.Green)) {
        Button(onClick = { viewModel.removeCheckedFromCart() }) {
            Text("⎚")
        }

        cartItemProperties.forEach { (recipeId, itemList) ->
            itemList.forEach { cartItem ->
                CartListElement(cartItem.cartItemProperties, viewModel)
            }
        }
        Autocomplete(chooseAction, autocompleteAction)
    }
}




