package de.moyapro.nushppinglist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import de.moyapro.nushppinglist.db.model.Recipe
import de.moyapro.nushppinglist.ui.RecipeListView
import de.moyapro.nushppinglist.ui.amountText
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import de.moyapro.nushppinglist.ui.util.createSampleRecipeCake
import org.junit.Rule
import org.junit.Test

class RecipeListViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun recipesAreListed() {
        val recipeNames = listOf("Cake", "Noodles", "Breakfast", "Lunch")
        createComposable(recipeNames.map { name -> createSampleRecipeCake(title = name) })

        recipeNames.forEach { name ->
            composeTestRule.onNodeWithText(name).assertIsDisplayed()
        }
    }

    @Test
    fun recipesAreExpandable() {
        val recipe = createSampleRecipeCake(title = "Cake")
        createComposable(listOf(recipe))
        composeTestRule.onNodeWithText(recipe.recipeProperties.title).performClick()
        recipe.recipeItems.forEach { item ->
            composeTestRule.onNodeWithText(item.item.name).assertIsDisplayed()
            composeTestRule.onNodeWithText(item.amount.toString()).assertIsDisplayed()
        }
        composeTestRule.onNodeWithText(recipe.recipeProperties.title).performClick()
        recipe.recipeItems.forEach { recipeItem ->
            composeTestRule.onNodeWithText(recipeItem.item.name).assertDoesNotExist()
            composeTestRule.onNodeWithText(amountText(recipeItem)).assertDoesNotExist()
        }
    }

    private fun createComposable(rezeptList: List<Recipe>) {
        composeTestRule.setContent {
            NuShppingListTheme {
                RecipeListView(rezeptList.toList())
            }
        }
    }
}
