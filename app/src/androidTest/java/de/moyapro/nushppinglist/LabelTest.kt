package de.moyapro.nushppinglist

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import de.moyapro.nushppinglist.ui.Label
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import org.junit.Rule
import org.junit.Test

internal class LabelTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun labelIsFound() {
        composeTestRule.setContent {
            NuShppingListTheme {
                Label("mylabel3000")
            }
        }
        composeTestRule.onAllNodesWithContentDescription(Label.DESCRIPTION).assertCountEquals(1)
    }

}