package de.moyapro.nushppinglist.view

import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import de.moyapro.nushppinglist.ui.component.Label
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import org.junit.Rule
import org.junit.Test

internal class LabelTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun labelIsFound() {
        val labelText = "mylabel3000"
        composeTestRule.setContent {
            NuShppingListTheme {
                Label(labelText)
            }
        }
        composeTestRule.onAllNodesWithContentDescription(Label.DESCRIPTION).assertCountEquals(1)
        composeTestRule.onAllNodesWithContentDescription(Label.DESCRIPTION)
            .assertAll(hasText(labelText))
    }

}
