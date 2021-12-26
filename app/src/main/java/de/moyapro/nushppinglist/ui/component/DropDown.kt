package de.moyapro.nushppinglist.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
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
    modifier: Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val itemIndex = values.indexOf(initialValue)
    var selectedIndex by remember { mutableStateOf(if (itemIndex == -1) 0 else itemIndex) }
    Surface(
        elevation = 0.dp,
        shape = RoundedCornerShape(20),
    ) {
        Row(
            modifier = modifier
                .clickable(onClick = { expanded = true })
        ) {
            val selectedText = itemLabel(values[selectedIndex])
            if (selectedText.isBlank() || selectedText == " ()") {
                Text(label,
                    modifier = Modifier
                        .padding(12.dp),
                    color = Color.LightGray
                )
            } else {
                Text(selectedText,
                    modifier = Modifier
                        .padding(12.dp)
                )

            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(.8F)
                .background(MaterialTheme.colors.background)

        ) {
            values.forEachIndexed { index, itemValue: T ->
                DropdownMenuItem(
                    onClick = {
                        selectedIndex = index
                        expanded = false
                        onValueChange(itemValue)
                    }
                ) {
                    Text(text = itemLabel(itemValue),
                        color = contentColorFor(backgroundColor = MaterialTheme.colors.background))
                }
            }
        }
    }
}
