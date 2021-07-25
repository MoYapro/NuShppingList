package de.moyapro.nushppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import de.moyapro.nushppinglist.ui.AppView
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import kotlinx.coroutines.FlowPreview

@FlowPreview
class MainActivity : ComponentActivity() {


    private val globalViewModel by viewModels<VM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        globalViewModel.add(CartItem("Milk"))
        globalViewModel.add(CartItem("Butter"))
        globalViewModel.add(CartItem("Eggs"))
        globalViewModel.add(Item("Toast"))
        setContent {
            NuShppingListTheme {
                AppView(globalViewModel)
            }
        }
    }


}