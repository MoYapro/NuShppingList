package de.moyapro.nushppinglist.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlusOne
import androidx.compose.material.icons.outlined.Dangerous
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
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

const val tag = "ItemList"

@FlowPreview
@Composable
@Preview
fun ItemList(@PreviewParameter(ItemListProvider::class) viewModel: CartViewModel) {
    val allItemList: List<Item> by viewModel.allItems.collectAsState(listOf())
    val cartItemPropertiesList: List<CartItemProperties> by viewModel.cartItems.collectAsState(
        listOf()
    )

    /**
     * selected cart is nullable because it might not have been initialized yet
     */
    val selectedCart: Cart? by viewModel.selectedCart.collectAsState(CONSTANTS.DEFAULT_CART)

    var filter: String by remember { mutableStateOf("") }
    var viewLocked: Boolean by remember { mutableStateOf(false) }
    val toggleLockedState = { viewLocked = !viewLocked }

    val cartItemList: List<CartItem> = allItemList
        .map { item ->
            val cartItemProperties =
                cartItemPropertiesList.firstOrNull { it.itemId == item.itemId && (selectedCart?.cartId == it.inCart) }
            CartItem(
                cartItemProperties ?: CartItemProperties(
                    newItemId = item.itemId,
                    inCart = selectedCart?.cartId
                        ?: CONSTANTS.DEFAULT_CART.cartId,
                    amount = 0
                ),
                item,
            )
        }
        .filter { it.item.name.lowercase().contains(filter.lowercase()) }
        .filter { !viewLocked || it.cartItemProperties.amount > 0 }
        .sortedWith(SortCartItemPairByCheckedAndName)

    if (SWITCHES.DEBUG) Debug(cartItemList, allItemList, selectedCart)

    val displayClearFilterFab = filter.trim().isNotBlank()
    val total: BigDecimal =
        cartItemList.map { it.item.price * BigDecimal(it.cartItemProperties.amount) }
            .sumByBigDecimal()


    val coroutineScope = rememberCoroutineScope()
    val clearFilter = { filter = "" }
    val updateFilter = { newFilter: String -> filter = newFilter }

    mainLayout(
        displayClearFilterFab,
        viewModel,
        viewLocked,
        filter,
        total,
        cartItemList,
        coroutineScope,
        clearFilter,
        updateFilter,
        toggleLockedState,
    )
}

@Composable
private fun Debug(
    cartItems: List<CartItem>,
    allItemList: List<Item>,
    selectedCart: Cart?,
) {
    Log.i(tag, "selected cart: $selectedCart")
    Column() {
        Text("all cart items:")
        cartItems.map {
            "${
                it.cartItemProperties.inCart?.id?.toString()?.substring(0..6)
            } - ${it.item.name} - ${it.cartItemProperties.amount} - ${it.cartItemProperties.checked}"
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
    displayCleanFilterFab: Boolean,
    viewModel: CartViewModel,
    viewLocked: Boolean,
    filter: String,
    total: BigDecimal,
    cartItemList: List<CartItem>,
    coroutineScope: CoroutineScope,
    clearFilter: () -> Unit,
    updateFilter: (String) -> Unit,
    toggleLockedState: () -> Unit,
) {
    val emptyFab: @Composable () -> Unit = {}

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        floatingActionButton = if (displayCleanFilterFab) {
            {
                CleanFilterFab(updateFilter)
            }
        } else {
            emptyFab
        },
        topBar = {
            itemTopBar(viewModel, total, viewLocked, toggleLockedState)
        },
        content = { innerPadding ->
            itemListView(innerPadding, cartItemList, viewModel, coroutineScope, viewLocked)
        },
        bottomBar = {
            FilterTextField(filter, viewModel, updateFilter)
        }
    )
}

@Composable
private fun CleanFilterFab(
    updateFilter: (String) -> Unit,
) {
    FloatingActionButton(onClick = {
        updateFilter("")
    }) {
        Icon(Icons.Filled.Clear, contentDescription = "filter entfernen")
    }
}

@Composable
private fun itemTopBar(
    viewModel: CartViewModel,
    total: BigDecimal,
    viewLocked: Boolean,
    toggleLockedState: () -> Unit,
) {
    Column() {
        CartSelector(viewModel)
        Row() {
            RemoveCheckedButton(viewModel)
            Spacer(modifier = Modifier.width(4.dp))
            LockButton(viewLocked, toggleLockedState)
            Spacer(modifier = Modifier.width(4.dp))
            SumDisplay(total)
        }
    }
}

@Composable
private fun LockButton(viewLocked: Boolean, toggleLockedState: () -> Unit) {
    Button(onClick = toggleLockedState) {
        when (viewLocked) {
            true -> Icon(Icons.Outlined.Lock, contentDescription = "Lock")
            false -> Icon(Icons.Outlined.LockOpen, contentDescription = "not Lock")
        }
    }
}

@Composable
private fun itemListView(
    innerPadding: PaddingValues,
    cartItemList: List<CartItem>,
    viewModel: CartViewModel,
    coroutineScope: CoroutineScope,
    viewLocked: Boolean,
) {
    val listState = rememberLazyListState()
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
                    viewLocked,
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
    viewModel: CartViewModel,
    updateFilter: (String) -> Unit,
) {
    val filterIsSet = filter.trim() != ""
    val addItemFromFilter = {
        viewModel.addToCart(filter.trim())
        updateFilter(
            if (MainActivity.preferences?.getBoolean(
                    SETTING.CLEAR_AFTER_ADD.name,
                    false
                ) == true
            )
                "" else filter.trim()
        )
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        EditTextField(
            initialValue = filter,
            onValueChange = updateFilter,
            widthPercentage = .8F,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            doneAction = addItemFromFilter
        )
        Button(
            modifier = Modifier
                .absolutePadding(top = 7.dp, left = 4.dp)
                .fillMaxWidth()
                .height(57.dp),
            shape = RoundedCornerShape(topStart = 4.dp),
            onClick = addItemFromFilter
        ) {
            when (filterIsSet) {
                true -> Icon(Icons.Filled.PlusOne, contentDescription = "Leeren")
                false -> Icon(Icons.Filled.Add, contentDescription = "Leeren")
            }
        }
    }
}

@Composable
private fun RemoveCheckedButton(viewModel: CartViewModel) {
    Button(onClick = { viewModel.removeCheckedFromCart() }) {
        Icon(Icons.Outlined.Dangerous, contentDescription = "entferne fertige")
    }
}
