package de.moyapro.nushppinglist

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import de.moyapro.nushppinglist.constants.SWITCHES
import de.moyapro.nushppinglist.constants.UNIT
import de.moyapro.nushppinglist.db.AppDatabase
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.ui.AppView
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.model.RecipeViewModel
import de.moyapro.nushppinglist.ui.model.ViewModelFactory
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import de.moyapro.nushppinglist.ui.util.createSampleRecipeCake
import de.moyapro.nushppinglist.ui.util.createSampleRecipeNoodels
import kotlinx.coroutines.*

@FlowPreview
class MainActivity : ComponentActivity() {

    val database by lazy { AppDatabase.getDatabase(this) }

    private val cartViewModel by viewModels<CartViewModel>() { ViewModelFactory(database) }
    private val recipeViewModel by viewModels<RecipeViewModel>() { ViewModelFactory(database) }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        super.onCreate(savedInstanceState)
        if(SWITCHES.INIT_DB_ON_BOOT) initTestData()
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
        val items = listOf(
            "EintestwoallesineinemlangenWortistundkeinLeerzeichenzumumbrechen",
            "Handkäse mit Pflaumen (Anmerkung) am besten die kleinen",
        )

        val cartItems = listOf(
            "Milch",
            "Butter",
            "Vanillekipferl",
            "Wasser",
            "Trinken",
            "Bier",
            "Waschmittel",
            "Hundefutter (Dose)",
            "Weintrauben",
            "UnicodeTest: ℰℯໂ६£Ē℮ē",
            "Eis",
            "Marmelade",
            "Chips",
            "Dounuts",
            "Gurken",
            "Tomaten",
            "Zwiebeln",
            "Rosinen",
            "Mehl",
            "Zucker",
            "Kaffee",
            "Tee",
            "Senf",
            "Salat",
            "Möhren",
            "Petersilie",
            "Basilikum",
            "Salz",
            "Apfel",
            "Banane",


            )

        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            database.clearAllTables()
            cartItems.map{CartItem(it)}.forEach(cartViewModel::add)
            items.map { Item(name = it, itemUnit = UNIT.KILOGRAMM) }.forEach(cartViewModel::add)
            recipeViewModel.save(createSampleRecipeCake())
            recipeViewModel.save(createSampleRecipeNoodels())
        }
    }


}
