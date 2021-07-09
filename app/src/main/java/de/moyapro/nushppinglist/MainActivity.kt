package de.moyapro.nushppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
                ItemList(viewModel)
            }
        }
    }
}