package de.moyapro.nushppinglist.view

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.ui.ItemList
import de.moyapro.nushppinglist.ui.ItemListElement
import de.moyapro.nushppinglist.ui.component.EditTextField
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import org.junit.Assert.*
import org.junit.Ignore
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
                    editMode = true,
                    subtractAction = viewModel::subtractFromCart
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
                ItemListElement(cartItem = existingItem,
                    addAction = action,
                    subtractAction = viewModel::subtractFromCart)
            }
        }
        composeTestRule.onNodeWithText("${CONSTANTS.CART_CHAR} x 1").performClick()
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
                ItemListElement(cartItem, subtractAction = viewModel::subtractFromCart)
            }
        }
        composeTestRule.onNodeWithText("${CONSTANTS.CART_CHAR} x 1").assertIsDisplayed()
    }

    @Test
    fun editInputsAreShown() {
        val name = "Milk"
        createComposable(CartItem(name), true)
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
        composeTestRule.onAllNodesWithText(name).assertCountEquals(2)
    }

    @Test
    @Ignore
    fun increaseAmountOnAddAgain() {
        val cartItem = CartItem("thing")
        val viewModel = CartViewModel()
        viewModel.add(cartItem)

        composeTestRule.setContent {
            NuShppingListTheme {
                ItemList(viewModel)
            }
        }

        composeTestRule.onNodeWithText("${CONSTANTS.CART_CHAR} x 1").assertIsDisplayed().performClick()
        Thread.sleep(10000)
        composeTestRule.onNodeWithText("${CONSTANTS.CART_CHAR} x 2").assertIsDisplayed()
    }


    private fun createComposable(cartItem: CartItem, editMode: Boolean = false) {
        composeTestRule.setContent {
            NuShppingListTheme {
                ItemListElement(cartItem = cartItem,
                    editMode = editMode,
                    subtractAction = viewModel::subtractFromCart)
            }
        }
    }
}
