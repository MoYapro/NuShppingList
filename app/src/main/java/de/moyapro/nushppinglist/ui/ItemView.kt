package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.ui.model.CartViewModel

@Composable
fun ItemView(viewModel: CartViewModel) {
    val scrollState = ScrollState(0)
    Column(Modifier
        .verticalScroll(scrollState)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            Arrangement.End
        ) {
            FloatingActionButton(
                onClick = { viewModel.add(Item("?")) }) {
                Icon(Icons.Filled.Add, contentDescription = "Hinzufügen")
            }
        }
        ItemList(viewModel)
    }
}
