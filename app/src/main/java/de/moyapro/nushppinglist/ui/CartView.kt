package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.RecipeId
import de.moyapro.nushppinglist.ui.component.Autocomplete
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.util.SortCartItemPairByCheckedAndName
import de.moyapro.nushppinglist.util.sumByBigDecimal
import java.math.BigDecimal


@OptIn(ExperimentalMaterialApi::class)
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
            .sortedWith(SortCartItemPairByCheckedAndName)

    val total: BigDecimal =
        cartItemProperties.map { it.second.item.price * BigDecimal(it.second.cartItemProperties.amount) }
            .sumByBigDecimal()

    Scaffold(
        Modifier.fillMaxHeight(),
        topBar = {
            Button(onClick = { viewModel.removeCheckedFromCart() }) {
                Text("⎚")
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
                        CartListElement(cartItem, viewModel)
                    }
                }
            }
        }
    )


}

@Composable
private fun SumDisplay(total: BigDecimal) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Gesamtpreis")
            Text(
                modifier = Modifier.absolutePadding(right = 21.dp),
                text = "$total €")
        }
    }
}




