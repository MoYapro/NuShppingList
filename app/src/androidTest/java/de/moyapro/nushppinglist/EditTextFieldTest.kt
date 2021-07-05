package de.moyapro.nushppinglist

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import de.moyapro.nushppinglist.ui.EditTextField
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import org.junit.Rule
import org.junit.Test

class EditTextFieldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun textEditIsFound() {
        var value = "1"
        composeTestRule.setContent {
            NuShppingListTheme {
                EditTextField("foobar", value) { x -> value = x }
            }
        }
        composeTestRule.onAllNodesWithContentDescription(EditTextField.DESCRIPTION)
            .assertCountEquals(1)
    }

}