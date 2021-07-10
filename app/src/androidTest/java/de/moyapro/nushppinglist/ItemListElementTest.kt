package de.moyapro.nushppinglist

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import de.moyapro.nushppinglist.ui.EditTextField
import de.moyapro.nushppinglist.ui.ItemListElement
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ItemListElementTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun saveExecutesSaveActionWithNewValue() {
        var saveActionCalled = false
        var itemText = ""
        val saveAction: (Item) -> Unit = { item ->
            itemText = item.name
            saveActionCalled = true
        }
        composeTestRule.setContent {
            NuShppingListTheme {
                ItemListElement(item = Item("Milk"), saveAction, true)
            }
        }
        val editField =
            composeTestRule.onAllNodesWithContentDescription(EditTextField.DESCRIPTION)[0]
        editField.performTextInput("2")
        composeTestRule.onNodeWithText("Save").performClick()
        assertTrue("Saveaction should be called", saveActionCalled)
        assertEquals("Itemname should be updated", "Milk2", itemText)
    }
}