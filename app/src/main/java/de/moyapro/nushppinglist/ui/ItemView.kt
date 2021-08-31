package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.moyapro.nushppinglist.Item
import de.moyapro.nushppinglist.VM

@Composable
fun ItemView(viewModel: VM) {
    Column {
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