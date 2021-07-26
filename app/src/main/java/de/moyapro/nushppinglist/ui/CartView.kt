package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import de.moyapro.nushppinglist.CartItemProperties
import de.moyapro.nushppinglist.Item
import de.moyapro.nushppinglist.VM


@Composable
fun CartView(viewModel: VM) {
    val cartItemProperties: List<CartItemProperties> by viewModel.cartItems.collectAsState(
        listOf()
    )
    val currentSearchText = remember { mutableStateOf("") }
    val autocompleteList = remember { mutableStateOf(emptyList<Item>()) }

    Column {
        cartItemProperties.forEach { item ->
            CartListElement(item, viewModel)
        }
        autocompleteList.value.forEach {
            Text(
                it.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Gray)
                    .clickable(
                        onClick = {
                            currentSearchText.value = it.name
                            autocompleteList.value = emptyList()
                        }
                    )
            )
        }
        Row {
            EditTextField(
                initialValue = currentSearchText.value,
                onValueChange = { newText: String ->
                    currentSearchText.value = newText.trim()
                    autocompleteList.value = if (currentSearchText.value.isBlank()) {
                        emptyList()
                    } else {
                        viewModel.getAutocompleteItems(currentSearchText.value)
                    }

                })
            Button(onClick = {
                viewModel.addToCart(currentSearchText.value)
                currentSearchText.value = ""
                autocompleteList.value = emptyList()
            }) {
                Label(labelText = "+")
            }
        }
    }
}