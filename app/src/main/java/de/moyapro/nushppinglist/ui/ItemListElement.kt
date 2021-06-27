package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.moyapro.nushppinglist.Item

@Composable
fun ItemListElement(item: Item) {

    Text(
        item.name,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {})
    )
}