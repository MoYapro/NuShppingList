package de.moyapro.nushppinglist.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun Autocomplete(
    chooseAction: (String) -> Unit,
    autocompleteAction: (String) -> List<String>,
) {

    var currentSearchText by remember { mutableStateOf("") }
    var autocompleteList by remember { mutableStateOf(emptyList<String>()) }
    val showAddActionButton = autocompleteList.isEmpty() && currentSearchText.trim().isNotBlank()

    val clearSearch = {
        currentSearchText = ""
        autocompleteList = emptyList()
    }

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
                                    clearSearch()
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
                    clearSearch()
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
                    currentSearchText = newText
                    autocompleteList = if (currentSearchText.trim().isBlank()) {
                        emptyList()
                    } else {
                        autocompleteAction(currentSearchText)
                    }

                },
                widthPercentage = .8F,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                doneAction = clearSearch
            )
            Button(
                modifier = Modifier
                    .absolutePadding(top = 7.dp, left = 4.dp)
                    .fillMaxWidth()
                    .height(57.dp),
                shape = RoundedCornerShape(topStart = 4.dp),
                onClick = clearSearch
            ) {
                Icon(Icons.Filled.Clear, contentDescription = "Leeren")
            }
        }
    }
}
