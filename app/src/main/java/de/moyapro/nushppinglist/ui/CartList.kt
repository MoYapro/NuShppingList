package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import de.moyapro.nushppinglist.MainActivity
import de.moyapro.nushppinglist.constants.SETTING
import de.moyapro.nushppinglist.db.model.Cart
import de.moyapro.nushppinglist.ui.component.EditTextField
import de.moyapro.nushppinglist.ui.model.CartViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CartList(viewModel: CartViewModel) {
    val allCartList: List<Cart> by viewModel.allCart.collectAsState(listOf())
    val listState = rememberLazyListState()
    var newCartName by remember { mutableStateOf("") }
    val displayNewCartFab = newCartName.trim().isNotBlank()


    val clearFilter: () -> Unit = {
        newCartName = if (MainActivity.preferences.getBoolean(SETTING.CLEAR_AFTER_ADD.name, false)
        )
            "" else newCartName.trim()
    }

    val saveAction: () -> Unit = {
        viewModel.add(Cart(cartName = newCartName.trim()))
        clearFilter()
    }

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        floatingActionButton = if (displayNewCartFab) {
            {
                FloatingActionButton(onClick = saveAction) {
                    Icon(Icons.Filled.Add, contentDescription = "Neu")
                }
            }
        } else {
            {} // emptyFab
        },
        content = { innerPadding ->
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
                    items(count = allCartList.size) { index ->
                        val cart = allCartList[index]
                        CartListElement(
                            cart = cart,
                            saveAction = viewModel::update,
                            deleteAction = viewModel::removeCart,
                        )

                    }
                    item { Spacer(modifier = Modifier.height(240.dp)) }
                }
            }
        },
        bottomBar = {
            Row(Modifier.fillMaxWidth()) {
                EditTextField(
                    label = "Name der Liste",
                    initialValue = newCartName,
                    onValueChange = { newCartName = it },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    widthPercentage = .8F,
                    doneAction = saveAction
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
    )
}
