package de.moyapro.nushppinglist

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import de.moyapro.nushppinglist.ui.Autocomplete
import de.moyapro.nushppinglist.ui.EditTextField
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AutocompleteTest {


    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun canAutocomplete() {
        var selectedValue = ""
        val elementText = "THE SELECTED ELEMENT"
        val textInput = "e"
        val chooseAction: (String) -> Unit = { selectedValue = it }
        val autocompleteAction: (String) -> List<String> = { listOf(elementText) }
        composeTestRule.setContent {
            NuShppingListTheme {
                Autocomplete(
                    chooseAction = chooseAction,
                    autocompleteAction = autocompleteAction
                )
            }
        }
        composeTestRule.onNodeWithText(elementText).assertDoesNotExist()
        val input = composeTestRule.onAllNodesWithContentDescription(EditTextField.DESCRIPTION)[0]
        input.performTextInput(textInput)
        composeTestRule.onNodeWithText(elementText).assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText("+").assertIsDisplayed().performClick()
        assertEquals("Should have selected the element", elementText, selectedValue)
    }
}