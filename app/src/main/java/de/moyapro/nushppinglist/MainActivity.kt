package de.moyapro.nushppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import de.moyapro.nushppinglist.constants.CONSTANTS.CHECKED
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.ui.AppView
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.model.RecipeViewModel
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import de.moyapro.nushppinglist.ui.util.createSampleRecipeCake
import de.moyapro.nushppinglist.ui.util.createSampleRecipeNoodels
import kotlinx.coroutines.FlowPreview

@FlowPreview
class MainActivity : ComponentActivity() {


    private val cartViewModel by viewModels<CartViewModel>()
    private val recipeViewModel by viewModels<RecipeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTestData()
        setContent {
            NuShppingListTheme {
                AppView(
                    cartViewModel,
                    recipeViewModel,
                )
            }
        }
    }

    private fun initTestData() {
        cartViewModel.add(CartItem("Milk"))
        cartViewModel.add(CartItem("Butter"))
        cartViewModel.add(CartItem("Eggs", CHECKED))
        cartViewModel.add(Item("Toast"))
        recipeViewModel.save(createSampleRecipeCake())
        recipeViewModel.save(createSampleRecipeNoodels())
    }


}
