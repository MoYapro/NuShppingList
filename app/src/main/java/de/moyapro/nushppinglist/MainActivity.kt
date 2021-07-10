package de.moyapro.nushppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import de.moyapro.nushppinglist.ui.ItemList
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import kotlinx.coroutines.FlowPreview

@FlowPreview
class MainActivity : ComponentActivity() {


    private val viewModel by viewModels<VM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NuShppingListTheme {
                Column {
                    Row(
                        Modifier.fillMaxWidth(),
                        Arrangement.End
                    ) {
                        FloatingActionButton(
                            onClick = { viewModel.add(Item("Milk")) },
                        ) {
                            Text(text = "+")
                        }
                    }
                    ItemList(viewModel)
                }
            }
        }
    }
}