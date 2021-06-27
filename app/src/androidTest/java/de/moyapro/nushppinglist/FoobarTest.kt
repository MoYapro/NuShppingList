package de.moyapro.nushppinglist

import androidx.compose.material.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

internal class FoobarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        composeTestRule.setContent {
            NuShppingListTheme {
                Text("foobar")
            }
        }
    }

    @Test
    fun test_TitleIsShown() {
        composeTestRule.onNodeWithText("foobar").assertIsDisplayed()
    }
}