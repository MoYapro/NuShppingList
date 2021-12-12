package de.moyapro.nushppinglist.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.ui.MainView
import de.moyapro.nushppinglist.ui.MainView.*
import de.moyapro.nushppinglist.ui.ViewSelector
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import org.junit.Rule
import org.junit.Test

class ViewSelectorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun allViewsAreDisplayed() {
        createComposable()
        composeTestRule.onNodeWithText("Einkaufsliste").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dinge").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rezepte").assertIsDisplayed()
    }

    @Test
    fun einkaufslisteSelectable() {
        createComposable(DINGE)
        composeTestRule.onNodeWithText(EINKAUFSLISTE.text).performClick()
        val activeElement = composeTestRule.onAllNodesWithContentDescription(CONSTANTS.ACTIVE)
        activeElement[0].assertTextContains(EINKAUFSLISTE.text)
    }

    @Test
    fun dingeSelectable() {
        createComposable(EINKAUFSLISTE)
        composeTestRule.onNodeWithText(DINGE.text).performClick()
        val activeElement = composeTestRule.onAllNodesWithContentDescription(CONSTANTS.ACTIVE)
        activeElement[0].assertTextContains(DINGE.text)

    }

    @Test
    fun rezepteSelectable() {
        createComposable(EINKAUFSLISTE)
        composeTestRule.onNodeWithText(REZEPTE.text).performClick()
        val activeElement = composeTestRule.onAllNodesWithContentDescription(CONSTANTS.ACTIVE)
        activeElement[0].assertTextContains(REZEPTE.text)
    }

    private fun createComposable(
        selectedView: MainView = EINKAUFSLISTE,
        updateAction: ((MainView) -> Unit)? = null,
    ) {
        composeTestRule.setContent {
            var view by remember { mutableStateOf(selectedView) }
            val action = updateAction ?: { view = it }
            NuShppingListTheme {
                ViewSelector(view, action)
            }
        }
    }
}
