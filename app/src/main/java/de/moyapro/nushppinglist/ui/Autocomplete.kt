package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
fun Autocomplete(
    chooseAction: (String) -> Unit,
    autocompleteAction: (String) -> List<String>,
) {

    val currentSearchText = remember { mutableStateOf("") }
    val autocompleteList = remember { mutableStateOf(emptyList<String>()) }

    Column(Modifier.background(color = Color.Red)) {
        Card(
            modifier = Modifier
                .padding(Dp(12F))
        ) {
            Column {
                autocompleteList.value.forEach {
                    Text(
                        it,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dp(4F))
                            .clickable(
                                onClick = {
                                    chooseAction(it)
                                    currentSearchText.value = ""
                                    autocompleteList.value = emptyList()
                                }
                            )
                    )
                }
            }
        }
        Row {
            EditTextField(
                initialValue = currentSearchText.value,
                onValueChange = { newText: String ->
                    currentSearchText.value = newText.trim()
                    autocompleteList.value = if (currentSearchText.value.isBlank()) {
                        emptyList()
                    } else {
                        autocompleteAction(currentSearchText.value)
                    }

                })
            Button(onClick = {
                chooseAction(currentSearchText.value)
                currentSearchText.value = ""
                autocompleteList.value = emptyList()
            }) {
                Label(labelText = "+")
            }
        }
    }
}
