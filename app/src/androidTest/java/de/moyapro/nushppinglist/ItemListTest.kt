package de.moyapro.nushppinglist

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import de.moyapro.nushppinglist.ui.ItemList
import de.moyapro.nushppinglist.ui.ItemListElement
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import org.junit.Rule
import org.junit.Test

internal class ItemListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showItemName() {
        val name = "Milk"
        createComposable(Item(name))
        composeTestRule.onNodeWithText(name).assertIsDisplayed()
    }

    @Test
    fun showItemList() {
        val names = listOf("Milk", "Apple")
        createComposable(names.map { Item(it) })
        names.forEach { name ->
            composeTestRule.onNodeWithText(name).assertIsDisplayed()
        }
    }

    @Test
    fun clickableItemList() {
        val name = "Milk"
        createComposable(Item(name))
        val itemNode = composeTestRule.onNodeWithText(name)
        itemNode.assertHasClickAction()
    }

    @Test
    fun switchToEditItem() {
        val name = "Milk"
        createComposable(Item(name))
        val itemNode = composeTestRule.onNodeWithText(name)
        composeTestRule.onNodeWithText("Save").assertDoesNotExist()
        itemNode.performClick()
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
    }


    private fun createComposable(item: Item) {
        composeTestRule.setContent {
            NuShppingListTheme {
                ItemListElement(item)
            }
        }
    }

    private fun createComposable(items: List<Item>) {
        composeTestRule.setContent {
            NuShppingListTheme {
                ItemList(items)
            }
        }
    }


}