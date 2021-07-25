package de.moyapro.nushppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import de.moyapro.nushppinglist.ui.ItemList
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import kotlinx.coroutines.FlowPreview

@FlowPreview
class MainActivity : ComponentActivity() {


    private val globalViewModel by viewModels<VM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        globalViewModel.add(Item("_Milk"))
        globalViewModel.add(Item("_Butter"))
        globalViewModel.add(Item("_Eggs"))
        globalViewModel.add(CartItem("Milk"))
        globalViewModel.add(CartItem("Butter"))
        globalViewModel.add(CartItem("Eggs"))
        setContent {
            NuShppingListTheme {
                AppView()
            }
        }
    }

    @Composable
    private fun AppView(showCart: Boolean = true) {
        var cartIsDisplayed: Boolean by remember { mutableStateOf(showCart) }
        Column {

            Button(onClick = {
                cartIsDisplayed = !cartIsDisplayed
            }) {
                Text("XXX")
            }

            if (cartIsDisplayed)
                CartView(globalViewModel)
            else
                ItemView(globalViewModel)
        }
    }

    @Composable
    private fun CartView(viewModel: VM) {
        val cartItemProperties: List<CartItemProperties> by this.globalViewModel.cartItems.collectAsState(
            listOf()
        )
        Column {
            cartItemProperties.forEach { item ->
                CartListElement(item, viewModel)
            }
        }
    }

    @Composable
    private fun CartListElement(
        cartItem: CartItemProperties,
        viewModel: VM
    ) {
        val item = viewModel.getItemByItemId(cartItem.itemId)
        if (null != item) {
            Row {
                Text(text = item.name)
            }
        } else {
            Text(text = "Unbekanntes Dings")
        }
    }

    @Composable
    private fun ItemView(viewModel: VM) {
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