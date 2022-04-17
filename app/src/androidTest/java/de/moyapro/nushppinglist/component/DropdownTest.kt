package de.moyapro.nushppinglist.component

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import de.moyapro.nushppinglist.ui.component.Dropdown
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import io.kotest.matchers.shouldBe
import org.junit.Rule
import org.junit.Test

class DropdownTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dropdownSelectElement__string() {
        var selected: String? = null;
        composeTestRule.setContent {
            NuShppingListTheme {
                Dropdown(
                    label = "The drop down",
                    initialValue = "world",
                    values = listOf("hallo", "world", null),
                    onValueChange = { newValue -> selected = newValue },
                )
            }
        }
        composeTestRule.onNodeWithText("world").assertIsDisplayed()
        composeTestRule.onNodeWithText("hallo").assertDoesNotExist()
        composeTestRule.onNodeWithText("world").performClick()
        composeTestRule.onAllNodesWithText("world").assertCountEquals(2)
        composeTestRule.onNodeWithText("hallo").assertIsDisplayed()
        composeTestRule.onNodeWithText("hallo").performClick()
        composeTestRule.onNodeWithText("world").assertDoesNotExist()
        composeTestRule.onNodeWithText("hallo").assertIsDisplayed()
        selected shouldBe "hallo"
    }

    @Test
    fun dropdownSelectElement__numbers() {
        val numbers: IntRange = 0..10
        var selected = 0
        composeTestRule.setContent {
            NuShppingListTheme {
                Dropdown(
                    label = "The drop down",
                    initialValue = selected,
                    values = numbers.toList() + listOf(11),
                    onValueChange = { newValue -> selected = newValue },
                    itemLabel = { "Item $it" },
                )
            }
        }
        repeat(numbers.count()) {
            composeTestRule.onNodeWithText("Item $selected").performClick()
            composeTestRule.onNodeWithText("Item ${selected + 1}").performClick()
        }
        selected shouldBe 11
    }


}
