package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.moyapro.nushppinglist.constants.SWITCHES
import de.moyapro.nushppinglist.db.model.Cart
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.RecipeId
import de.moyapro.nushppinglist.ui.component.Autocomplete
import de.moyapro.nushppinglist.ui.component.Dropdown
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

@Composable
private fun CartSelector(viewModel: CartViewModel) {
    val carts: List<Cart?> by viewModel.allCart.collectAsState(listOf())
    val cartsAndEmpty = listOf(null) + carts


    var selectedCart by remember { mutableStateOf(viewModel.getSelectedCart()) }

Column() {

    Dropdown(
        label = "Alle Listen",
        initialValue = selectedCart,
        values = cartsAndEmpty,
        onValueChange = {
            selectedCart = it
            viewModel.selectCart(it)
        },
        itemLabel = { it?.cartName ?: "Alle Listen" },
        modifier = Modifier.fillMaxWidth()
    )
    if(SWITCHES.DEBUG) {
        Text(selectedCart.toString())
    }
}
}




