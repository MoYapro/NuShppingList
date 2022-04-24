package de.moyapro.nushppinglist.component

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.performTextInput
import de.moyapro.nushppinglist.ui.component.EditTextField
import de.moyapro.nushppinglist.ui.component.EditTextField.DESCRIPTION
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import io.kotest.matchers.shouldBe
import org.junit.Rule
import org.junit.Test

class EditTextFieldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun textEditIsEditable() {
        val initialValue = "original"
        var actionValue = mutableListOf<String>()
        val textInput = "input"
        val expectedOutput = initialValue + textInput
        composeTestRule.setContent {
            NuShppingListTheme {
                EditTextField(
                    label = "Label",
                    initialValue = initialValue,
                    onValueChange = { x ->
                        actionValue += x
                    })
            }
        }
        val fields =
            composeTestRule.onAllNodesWithContentDescription(DESCRIPTION)
        fields.assertCountEquals(1)
        val editField = fields[0]
        editField.performTextInput(textInput)
        Thread.sleep(100)
        actionValue.last() shouldBe expectedOutput
    }

}
