package de.moyapro.nushppinglist.util

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithText

fun List<String>.assertIsDisplayed(composeTestRule: ComposeContentTestRule) {
    this.forEach { text ->
        composeTestRule.onNodeWithText(text).assertIsDisplayed()
    }
}

fun List<String>.assertDoesNotExist(composeTestRule: ComposeContentTestRule) {
    this.forEach { text ->
        composeTestRule.onNodeWithText(text).assertDoesNotExist()
    }

}
