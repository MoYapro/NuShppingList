package de.moyapro.nushppinglist.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Autocomplete(
    chooseAction: (String) -> Unit,
    autocompleteAction: (String) -> List<String>,
) {

    var currentSearchText by remember { mutableStateOf("") }
    var autocompleteList by remember { mutableStateOf(emptyList<String>()) }
    val showAddActionButton = autocompleteList.isEmpty() && currentSearchText.trim().isNotBlank()

    Column {
        Card(
            modifier = Modifier
                .padding(Dp(12F))
        ) {
            Column {
                autocompleteList.forEach {
                    Text(
                        it,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dp(6F))
                            .clickable(
                                onClick = {
                                    chooseAction(it)
                                    currentSearchText = ""
                                    autocompleteList = emptyList()
                                }
                            )
                    )
                }
            }
        }
        if (showAddActionButton) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                FloatingActionButton(onClick = {
                    chooseAction(currentSearchText)
                    currentSearchText = ""
                    autocompleteList = emptyList()
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Neu")
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            EditTextField(
                initialValue = currentSearchText,
                onValueChange = { newText: String ->
                    currentSearchText = newText.trim()
                    autocompleteList = if (currentSearchText.isBlank()) {
                        emptyList()
                    } else {
                        autocompleteAction(currentSearchText)
                    }

                },
                widthPercentage = .8F
            )
            Button(
                modifier = Modifier
                    .absolutePadding(top = 7.dp, left = 4.dp)
                    .fillMaxWidth()
                    .height(57.dp),
                shape = RoundedCornerShape(topStart = 4.dp),
                onClick = { currentSearchText = "" }
            ) {
                Icon(Icons.Filled.Clear, contentDescription = "Leeren")
            }
        }
    }
}
