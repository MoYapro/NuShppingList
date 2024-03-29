package de.moyapro.nushppinglist

import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import de.moyapro.nushppinglist.constants.CONSTANTS.PREFERENCES_FILE_NAME
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
import kotlinx.coroutines.*

@FlowPreview
class MainActivity : ComponentActivity() {

    val database by lazy { AppDatabase.getDatabase(this) }

    private val cartViewModel by viewModels<CartViewModel>() { ViewModelFactory(database) }
    private val recipeViewModel by viewModels<RecipeViewModel>() { ViewModelFactory(database) }

    companion object {
        lateinit var preferences: SharedPreferences
        lateinit var clipboardManager: ClipboardManager

        fun makeToast(text: String, context: Context) {
            Toast.makeText(
                context,
                text,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        preferences = getSharedPreferences(PREFERENCES_FILE_NAME, MODE_PRIVATE);
        clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        if (SWITCHES.INIT_DB_ON_BOOT) initTestData()
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
            cartItems.map { CartItem(it) }.forEach(cartViewModel::add)
            items.map { Item(name = it, itemUnit = UNIT.values().random()) }
                .forEach(cartViewModel::add)
//            recipeViewModel.save(createSampleRecipeCake())
//            recipeViewModel.save(createSampleRecipeNoodels())
        }
    }


}
