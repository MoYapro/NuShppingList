package de.moyapro.nushppinglist.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import de.moyapro.nushppinglist.MainActivity
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.constants.SETTING
import de.moyapro.nushppinglist.constants.SWITCHES
import de.moyapro.nushppinglist.db.model.Cart
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.CartItemProperties
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.ui.component.CartSelector
import de.moyapro.nushppinglist.ui.component.EditTextField
import de.moyapro.nushppinglist.ui.component.SumDisplay
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.util.ItemListProvider
import de.moyapro.nushppinglist.util.SortCartItemPairByCheckedAndName
import de.moyapro.nushppinglist.util.sumByBigDecimal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import java.math.BigDecimal

@FlowPreview
@Composable
@Preview
fun ItemList(@PreviewParameter(ItemListProvider::class) viewModel: CartViewModel) {
    val allItemList: List<Item> by viewModel.allItems.collectAsState(listOf())
    val cartItems: List<CartItem> by viewModel.allCartItems.collectAsState(listOf())
    val selectedCart: Cart by viewModel.selectedCart.collectAsState(CONSTANTS.DEFAULT_CART)
    if (SWITCHES.DEBUG) debug(cartItems, allItemList, selectedCart)

    var filter: String by remember { mutableStateOf("") }
    val filteredItems = allItemList.filter { it.name.lowercase().contains(filter.lowercase()) }
    val cartItemList: List<CartItem> = filteredItems
        .map { item ->
            val cartItem =
                cartItems.firstOrNull { it.item.itemId == item.itemId && (selectedCart?.cartId == it.cartItemProperties.inCart) }
            cartItem
                ?: CartItem(
                    CartItemProperties(
                        newItemId = item.itemId,
                        inCart = selectedCart.cartId,
                        amount = 0
                    ),
                    item,
                )
        }
        .sortedWith(SortCartItemPairByCheckedAndName)
    val listState = rememberLazyListState()
    val displayNewItemFab = filter.trim().isNotBlank() && cartItemList.isEmpty()
    val total: BigDecimal =
        cartItemList.map { it.item.price * BigDecimal(it.cartItemProperties.amount) }
            .sumByBigDecimal()

    Log.d("ItemList", listState.firstVisibleItemIndex.toString())

    val coroutineScope = rememberCoroutineScope()
    val clearFilter = { filter = "" }
    val updateFilter = { newFilter: String -> filter = newFilter }

    mainLayout(
        displayNewItemFab,
        viewModel,
        filter,
        total,
        listState,
        cartItemList,
        coroutineScope,
        clearFilter,
        updateFilter,
    )
}

@Composable
private fun debug(
    cartItems: List<CartItem>,
    allItemList: List<Item>,
    selectedCart: Cart?,
) {
    Column() {
        Text("all cart items:")
        cartItems.map {
            "${
                it.cartItemProperties.inCart?.id?.toString()?.substring(0..6)
            } - ${it.item.name} - ${it.cartItemProperties.amount}"
        }.forEach {
            Text(it)
        }
        Text("all items")
        Text(allItemList.joinToString { it.name })
        Text("selected cart: ${selectedCart?.cartName}")
    }
}

@Composable
private fun mainLayout(
    displayNewItemFab: Boolean,
    viewModel: CartViewModel,
    filter: String,
    total: BigDecimal,
    listState: LazyListState,
    cartItemList: List<CartItem>,
    coroutineScope: CoroutineScope,
    clearFilter: () -> Unit,
    updateFilter: (String) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        floatingActionButton = if (displayNewItemFab) {
            {
                FloatingActionButton(onClick = {
                    viewModel.addToCart(filter.trim())
                    updateFilter(
                        if (MainActivity.preferences?.getBoolean(SETTING.CLEAR_AFTER_ADD.name,
                                false) == true
                        )
                            "" else filter.trim()
                    )
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Neu")
                }
            }
        } else {
            {} // emptyFab
        },
        topBar = {
            itemTopBar(viewModel, total)
        },
        content = { innerPadding ->
            itemListView(innerPadding, listState, cartItemList, viewModel, coroutineScope)
        },
        bottomBar = {
            FilterTextField(filter, clearFilter, updateFilter)
        }
    )
}

@Composable
private fun itemTopBar(
    viewModel: CartViewModel,
    total: BigDecimal,
) {
    Column() {
        Row() {
            removeCheckedButton(viewModel)
            CartSelector(viewModel)
        }
        SumDisplay(total)
    }
}

@Composable
private fun itemListView(
    innerPadding: PaddingValues,
    listState: LazyListState,
    cartItemList: List<CartItem>,
    viewModel: CartViewModel,
    coroutineScope: CoroutineScope,
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        contentAlignment = Alignment.BottomEnd
    ) {
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(1.dp),
            state = listState,
        ) {
            items(count = cartItemList.size) { index ->
                val cartItem = cartItemList[index]
                ItemListElement(
                    cartItem = cartItem,
                    toggleCheckAction = viewModel::toggleChecked,
                    saveAction = viewModel::update,
                    addAction = viewModel::addToCart,
                    deleteAction = viewModel::removeItem,
                    subtractAction = viewModel::subtractFromCart,
                    scrollIntoViewAction = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(index)
                        }
                    }
                )
            }
            item { Spacer(modifier = Modifier.height(240.dp)) }
        }
    }
}

@Composable
private fun FilterTextField(
    filter: String,
    clearFilter: () -> Unit,
    updateFilter: (String) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        EditTextField(
            initialValue = filter,
            onValueChange = updateFilter,
            widthPercentage = .8F,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            doneAction = clearFilter
        )
        Button(
            modifier = Modifier
                .absolutePadding(top = 7.dp, left = 4.dp)
                .fillMaxWidth()
                .height(57.dp),
            shape = RoundedCornerShape(topStart = 4.dp),
            onClick = clearFilter
        ) {
            Icon(Icons.Filled.Clear, contentDescription = "Leeren")
        }
    }
}

@Composable
private fun removeCheckedButton(viewModel: CartViewModel) {
    Button(onClick = { viewModel.removeCheckedFromCart() }) {
        Text("⎚")
    }
}
