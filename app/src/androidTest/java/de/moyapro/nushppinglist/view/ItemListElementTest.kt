package de.moyapro.nushppinglist.view

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.ui.EditTextField
import de.moyapro.nushppinglist.ui.ItemList
import de.moyapro.nushppinglist.ui.ItemListElement
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class ItemListElementTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun saveExecutesSaveActionWithNewValue() {
        var saveActionCalled = false
        var itemText = ""
        val saveAction: (Item) -> Unit = { item ->
            itemText = item.name
            saveActionCalled = true
        }
        composeTestRule.setContent {
            NuShppingListTheme {
                ItemListElement(
                    cartItem = CartItem("Milk"),
                    saveAction = saveAction,
                    editMode = true
                )
            }
        }
        val editField =
            composeTestRule.onAllNodesWithContentDescription(EditTextField.DESCRIPTION)[0]
        editField.performClick()
        Thread.sleep(1000)
        editField.performTextInput("Milk2")
        Thread.sleep(1000)
        composeTestRule.onNodeWithText("Save").performClick()
        Thread.sleep(1000)
        assertTrue("Saveaction should be called", saveActionCalled)
        assertEquals("Itemname should be updated", "Milk2", itemText)
    }

    @Test
    fun addItemToCart() {
        var addedItem: Item? = null
        val existingItem = CartItem("newItem")
        val action: (Item) -> Unit = { item -> addedItem = item }
        composeTestRule.setContent {
            NuShppingListTheme {
                ItemListElement(cartItem = existingItem, addAction = action)
            }
        }
        Thread.sleep(5000)
        composeTestRule.onNodeWithText("🛒 x 1").performClick()
        assertNotNull("add action should be called", addedItem)
        assertEquals("Should have added correct item", existingItem.item, addedItem)
    }

    @Test
    fun showAmountInCartOnButton() {
        val cartItem = CartItem("thing")
        val viewModel = CartViewModel()
        viewModel.add(cartItem)

        composeTestRule.setContent {
            NuShppingListTheme {
                ItemListElement(cartItem)
            }
        }
        composeTestRule.onNodeWithText("🛒 x 1").assertIsDisplayed()
    }

    @Test
    fun increaseAmountOnAddAgain() {
        val cartItem = CartItem("thing")
        val viewModel = CartViewModel()
        viewModel.add(cartItem)

        composeTestRule.setContent {
            NuShppingListTheme {
                ItemList(viewModel)
            }
        }

        composeTestRule.onNodeWithText("🛒 x 1").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText("🛒 x 2").assertIsDisplayed()
    }

}
