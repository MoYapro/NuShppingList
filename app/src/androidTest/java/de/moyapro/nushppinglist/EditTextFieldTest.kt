package de.moyapro.nushppinglist

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.performTextInput
import de.moyapro.nushppinglist.ui.EditTextField
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class EditTextFieldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun textEditIsEditable() {
        val initialValue = "original"
        var value = initialValue
        val textInput = "input"
        composeTestRule.setContent {
            NuShppingListTheme {
                EditTextField("Label", initialValue) { x -> value = x }
            }
        }
        val fields =
            composeTestRule.onAllNodesWithContentDescription(EditTextField.DESCRIPTION)
        fields.assertCountEquals(1)
        val editField = fields[0]
        editField.performTextInput(textInput)
        assertEquals("Should update initialValue", initialValue + textInput, value)
    }

}