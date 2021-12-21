package de.moyapro.nushppinglist.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
    Card(
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart),
        shape = RoundedCornerShape(20),

        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { expanded = true })
        ) {
            val selectedText = itemLabel(values[selectedIndex])
            if (selectedText.isBlank() || selectedText == " ()") {
                Text(label,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    color = Color.LightGray
                )
            } else {
                Text(selectedText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                )

            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(.8F)
        ) {
            values.forEachIndexed { index, itemValue: T ->
                DropdownMenuItem(
                    onClick = {
                        selectedIndex = index
                        expanded = false
                        onValueChange(itemValue)
                    }
                ) {
                    Text(itemLabel(itemValue))
                }
            }
        }
    }
}
