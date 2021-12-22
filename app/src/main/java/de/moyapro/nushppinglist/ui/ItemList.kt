package de.moyapro.nushppinglist.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.CartItemProperties
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.ui.component.EditTextField
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.util.ItemListProvider
import de.moyapro.nushppinglist.util.CartItemByCheckedAndName
import kotlinx.coroutines.launch

@Composable
@Preview
fun ItemList(@PreviewParameter(ItemListProvider::class) viewModel: CartViewModel) {
    val allItemList: List<Item> by viewModel.allItems.collectAsState(listOf())
    val cartItems: List<CartItem> by viewModel.allCartItems.collectAsState(
        listOf()
    )

    var filter: String by remember { mutableStateOf("") }
// is filtering on cartItemList executed every frame?
    val cartItemList: List<CartItem> = allItemList
        .filter { it.name.lowercase().contains(filter.lowercase()) }
        .map { item ->
            val cartItem = cartItems.firstOrNull { it.item.itemId == item.itemId }
            cartItem
                ?: CartItem(
                    CartItemProperties(
                        newItemId = item.itemId,
                        amount = 0
                    ),
                    item,
                )
        }.sortedWith(CartItemByCheckedAndName)
    val listState = rememberLazyListState()
    val displayNewItemFab = filter.trim().isNotBlank() && cartItemList.isEmpty()

    Log.i("ItemList", listState.firstVisibleItemIndex.toString())

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        floatingActionButton = if (displayNewItemFab) {
            {
                FloatingActionButton(onClick = {
                    viewModel.addToCart(filter.trim())
                    filter = filter.trim()
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Neu")
                }
            }
        } else {
            {} // emptyFab
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.BottomEnd
            ) {
                LazyColumn(
                    modifier = Modifier.padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    state = listState,
                ) {
                    items(count = cartItemList.size) { index ->
                        val cartItem = cartItemList[index]
                        ItemListElement(
                            cartItem = cartItem,
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
                    item { Spacer(modifier = Modifier.height(220.dp)) }
                }
            }
        },
        bottomBar = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                EditTextField(initialValue = filter,
                    onValueChange = { filter = it },
                    widthPercentage = .8F)
                Button(
                    modifier = Modifier
                        .absolutePadding(top = 7.dp, left = 4.dp)
                        .fillMaxWidth()
                        .height(57.dp),
                    onClick = { filter = "" }
                ) {
                    Icon(Icons.Filled.Clear, contentDescription = "Leeren")
                }
            }
        }
    )
}
