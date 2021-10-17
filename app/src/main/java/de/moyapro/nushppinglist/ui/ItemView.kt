package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.ui.model.CartViewModel

@Composable
fun ItemView(viewModel: CartViewModel) {
    Column(Modifier.background(color = Color.Cyan)) {
        Row(
            Modifier.fillMaxWidth(),
            Arrangement.End
        ) {
            FloatingActionButton(
                onClick = { viewModel.add(Item("?")) },
            ) {
                Text(text = "+")
            }
        }
        ItemList(viewModel)
    }
}
