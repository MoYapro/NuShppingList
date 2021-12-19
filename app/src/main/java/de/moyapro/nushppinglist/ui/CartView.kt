package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
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
    val scrollState = ScrollState(0)
    val cartItemProperties: Map<RecipeId?, List<CartItem>> by collectAsState
    val chooseAction: (String) -> Unit = viewModel::addToCart
    val autocompleteAction: (String) -> List<String> = { searchString ->
        viewModel.getAutocompleteItems(searchString)
    }
    Scaffold(
        Modifier.fillMaxHeight(),
        topBar = {
            Button(onClick = { viewModel.removeCheckedFromCart() }) {
                Text("⎚")
            }
        },
        bottomBar = {
            Box {
                Autocomplete(chooseAction, autocompleteAction)
            }
        },
    ) {
        Column(Modifier
            .background(color = Color.Green)
            .verticalScroll(scrollState)
        ) {
            cartItemProperties.forEach { (recipeId, itemList) ->
                itemList.forEach { cartItem ->
                    CartListElement(cartItem.cartItemProperties, viewModel)
                }
            }
        }
    }


}




