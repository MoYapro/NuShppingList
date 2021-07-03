package de.moyapro.nushppinglist

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
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

    @Test
    fun editInputsAreShown() {
        val name = "Milk"
        createComposable(Item(name), true)
        val itemNode = composeTestRule.onNodeWithText(name)
        composeTestRule.onAllNodes(hasContentDescription("foobar")).assertCountEquals(1)
        composeTestRule.onRoot().printToLog("TAG", 3)
        composeTestRule.onNodeWithText("Save").assertDoesNotExist()
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
        itemNode.performClick()
    }


    private fun createComposable(item: Item, editMode: Boolean = false) {
        composeTestRule.setContent {
            NuShppingListTheme {
                ItemListElement(item, editMode)
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

    fun hasLabel(
        value: String,
        substring: Boolean = false,
        ignoreCase: Boolean = false
    ): SemanticsMatcher {
        return if (substring) {
            SemanticsMatcher(
                "${SemanticsProperties.ContentDescription.name} contains '$value' " +
                        "(ignoreCase: $ignoreCase)"
            ) {
                it.config.getOrNull(SemanticsProperties.ContentDescription)
                    ?.any { item -> item.contains(value, ignoreCase) } ?: false
            }
        } else {
            SemanticsMatcher(
                "${SemanticsProperties.ContentDescription.name} = '$value' (ignoreCase: $ignoreCase)"
            ) {
                it.config.getOrNull(SemanticsProperties.ContentDescription)
                    ?.any { item -> item.equals(value, ignoreCase) } ?: false
            }
        }
    }


}