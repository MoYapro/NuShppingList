package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.RecipeId
import de.moyapro.nushppinglist.ui.component.Autocomplete
import de.moyapro.nushppinglist.ui.component.CartSelector
import de.moyapro.nushppinglist.ui.component.SumDisplay
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.util.SortCartItemPairByCheckedAndNameRecipe
import de.moyapro.nushppinglist.util.sumByBigDecimal
import java.math.BigDecimal


@OptIn(ExperimentalMaterialApi::class)
@Deprecated("Use ItemList instead")
@Composable
fun CartView(viewModel: CartViewModel) {
    val collectAsState: State<Map<RecipeId?, List<CartItem>>> =
        viewModel.allCartItemsGrouped.collectAsState(
            mapOf()
        )
    val cartItemPropertiesMap: Map<RecipeId?, List<CartItem>> by collectAsState

    val cartItemProperties: List<Pair<RecipeId?, CartItem>> =
        cartItemPropertiesMap.map { (recipeId, itemList) ->
            itemList.map { cartItem ->
                recipeId to cartItem
            }
        }
            .flatten()
            .sortedWith(SortCartItemPairByCheckedAndNameRecipe)

    val total: BigDecimal =
        cartItemProperties.map { it.second.item.price * BigDecimal(it.second.cartItemProperties.amount) }
            .sumByBigDecimal()

    Scaffold(
        Modifier.fillMaxHeight(),
        topBar = {
            Row() {
                Button(onClick = { viewModel.removeCheckedFromCart() }) {
                    Text("⎚")
                }
                CartSelector(viewModel)
            }
        },
        bottomBar = {
            Autocomplete(
                chooseAction = viewModel::addToCart,
                autocompleteAction = viewModel::getAutocompleteItems
            )
        },
        content = { innerPadding ->
            Column(Modifier.fillMaxWidth()) {
                SumDisplay(total)
                LazyColumn(
                    modifier = Modifier.padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(count = cartItemProperties.size) { index ->
                        val (recipeId, cartItem) = cartItemProperties[index]
                        CartListElement(cartItem, viewModel::toggleChecked)
                    }
                }
            }
        }
    )


}





