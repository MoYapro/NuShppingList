package de.moyapro.nushppinglist.view

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.db.model.Recipe
import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.ui.RecipeListView
import de.moyapro.nushppinglist.ui.amountText
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.model.RecipeViewModel
import de.moyapro.nushppinglist.ui.model.ViewModelFactory
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import de.moyapro.nushppinglist.ui.util.createSampleRecipeCake
import de.moyapro.nushppinglist.util.DbTestHelper
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@Ignore("Recipes are disabled currently - will be available in the future")
class RecipeListViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    val database = DbTestHelper.createTestDatabase()

    val publisher: Publisher? = null

    @Before
    fun setup() {
        database.clearAllTables()
    }

    @Test
    fun recipesAreListed() {
        val recipeNames = listOf("Cake", "Noodles", "Breakfast", "Lunch")
        createComposable(recipeNames.map { name -> createSampleRecipeCake(title = name) })

        recipeNames.forEach { name ->
            composeTestRule.onNodeWithText(name).assertIsDisplayed()
        }
    }

    @Test
    fun recipesAreExpandableAndCollapsable() {
        val recipe = createSampleRecipeCake(title = "Cake")
        createComposable(listOf(recipe))
        composeTestRule.onNodeWithText(recipe.recipeProperties.title).performClick()
        recipe.recipeItems.forEach { item ->
            composeTestRule.onNodeWithText(item.item.name).assertIsDisplayed()
        }
        composeTestRule.onNodeWithText(recipe.recipeProperties.title).performClick()
        recipe.recipeItems.forEach { recipeItem ->
            composeTestRule.onNodeWithText(recipeItem.item.name).assertDoesNotExist()
            composeTestRule.onNodeWithText(amountText(recipeItem)).assertDoesNotExist()
        }
    }

    @Test
    fun addSelectionIsShown() {
        val recipe = createSampleRecipeCake(title = "Cake")
        createComposable(listOf(recipe))
        composeTestRule.onNodeWithText(recipe.recipeProperties.title).performClick()
        composeTestRule.onNodeWithText(CONSTANTS.CART_CHAR).performClick()

    }

    private fun createComposable(rezeptList: List<Recipe>) {
        val recipeViewModel = ViewModelFactory(database, publisher).create(RecipeViewModel::class.java)
        val cartViewModel = ViewModelFactory(database, publisher).create(CartViewModel::class.java)
        recipeViewModel.save(*rezeptList.toTypedArray())
        composeTestRule.setContent {
            NuShppingListTheme {
                RecipeListView(recipeViewModel, cartViewModel)
            }
        }
    }
}
