package de.moyapro.nushppinglist

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import de.moyapro.nushppinglist.constants.*
import de.moyapro.nushppinglist.constants.CONSTANTS.PREFERENCES_FILE_NAME
import de.moyapro.nushppinglist.db.AppDatabase
import de.moyapro.nushppinglist.db.ids.CartId
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.service.BackgroundSyncService
import de.moyapro.nushppinglist.sync.MqttSingleton
import de.moyapro.nushppinglist.ui.AppView
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.model.RecipeViewModel
import de.moyapro.nushppinglist.ui.model.ViewModelFactory
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import de.moyapro.nushppinglist.ui.util.createSampleRecipeCake
import de.moyapro.nushppinglist.ui.util.createSampleRecipeNoodels
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.take
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

@FlowPreview
class MainActivity : ComponentActivity() {

    private val tag = MainActivity::class.simpleName
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val cartViewModel by viewModels<CartViewModel>() {
        ViewModelFactory(database, MqttSingleton.adapter)
    }
    private val recipeViewModel by viewModels<RecipeViewModel>() {
        ViewModelFactory(database,
            null)
    }

    companion object {
        var preferences: SharedPreferences? = null

        fun makeToast(text: String, context: Context) {
            Toast.makeText(
                context,
                text,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(tag, "create onCreate")
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        preferences = getSharedPreferences(PREFERENCES_FILE_NAME, MODE_PRIVATE);

        initData()
        setContent {
            NuShppingListTheme {
                AppView(
                    cartViewModel,
                    recipeViewModel,
                    this.applicationContext
                )
            }
        }
        if (preferences?.getBoolean(SETTING.SYNC_ENABLED.name, false) == true) {
            applicationContext.startService(Intent(applicationContext,
                BackgroundSyncService::class.java))
        }

    }

    @OptIn(ExperimentalTime::class)
    private fun initData() {
        Log.d(tag, "start initTestData")

        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            database.clearAllTables()
            if (MainView.REZEPTE.enabled) {
                recipeViewModel.save(createSampleRecipeCake())
                recipeViewModel.save(createSampleRecipeNoodels())
            }
            var defaultCart = cartViewModel.allCart.take(1).firstOrNull()?.firstOrNull()
            if (SWITCHES.CREATE_DEFAULT_LIST && null == defaultCart) {
                Log.i(tag, "Create default cart")
                cartViewModel.add(CONSTANTS.DEFAULT_CART)
                delay(200.milliseconds)
            }
            defaultCart = cartViewModel.allCart.take(1).firstOrNull()?.firstOrNull()
            if (SWITCHES.INIT_DB_ON_BOOT && null != defaultCart) {
                createTestData(defaultCart.cartId)
            }
        }
        Log.d(tag, "done initTestData")
    }

    private fun createTestData(cartId: CartId) {

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

        cartItems.map {
            CartItem(
                newItemName = it,
                inCart = cartId,
                checked = false,
                newItemId = ItemId()
            )
        }.forEach(cartViewModel::add)
        items.map { Item(name = it, itemUnit = UNIT.values().random()) }
            .forEach(cartViewModel::add)
    }


}
