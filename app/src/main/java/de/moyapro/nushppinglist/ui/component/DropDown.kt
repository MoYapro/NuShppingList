package de.moyapro.nushppinglist.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun <T> Dropdown(
    label: String,
    initialValue: T,
    values: List<T>,
    onValueChange: (T) -> Unit,
    itemLabel: (T) -> String = { it.toString() },
) {
    var expanded by remember { mutableStateOf(false) }
    val itemIndex = values.indexOf(initialValue)
    var selectedIndex by remember { mutableStateOf(if (itemIndex == -1) 0 else itemIndex) }
    Box(modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.TopStart)) {
        Text(itemLabel(values[selectedIndex]),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { expanded = true })
                .background(
                    Color.Gray))
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color.Red)
        ) {
            values.forEachIndexed { index, itemValue: T ->
                DropdownMenuItem(onClick = {
                    selectedIndex = index
                    expanded = false
                    onValueChange(itemValue)
                }) {
                    Text(itemLabel(itemValue))
                }
            }
        }
    }
}
