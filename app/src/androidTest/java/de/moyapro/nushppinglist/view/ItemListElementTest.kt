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
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
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
                )
            }
        }
        val editField =
            composeTestRule.onAllNodesWithContentDescription(EditTextField.DESCRIPTION)[0]
        editField.performClick()
        editField.performTextInput("Milk2")
        composeTestRule.onAllNodesWithContentDescription("Hinzufügen")[1].assertHasClickAction()
            .performClick()
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
                ItemListElement(
                    cartItem = existingItem,
                    addAction = action
                )
            }
        }
        composeTestRule.onAllNodesWithContentDescription("Hinzufügen")[0].performClick()
        assertNotNull("add action should have been called", addedItem)
        assertEquals("Should have added correct item", existingItem.item, addedItem)
    }

    @Test
    fun showAmountInCartOnButton(): Unit = runBlocking {
        val cartItem = CartItem("thing")
        val viewModel = CartViewModel()
        var lastEditItem: Item? = null
        viewModel.add(cartItem)

        composeTestRule.setContent {
            NuShppingListTheme {
                ItemListElement(cartItem,
                    subtractAction = viewModel::subtractFromCart,
                    addAction = { itemToSave ->
                        lastEditItem = itemToSave
                        viewModel.addToCart(itemToSave)
                    }
                )
            }
        }
        Thread.sleep(Long.MAX_VALUE)
        val addToCartButton = composeTestRule.onAllNodesWithContentDescription("Hinzufügen")[0]
        addToCartButton.assertTextContains("x 1")
        Thread.sleep(2000)
        addToCartButton.performClick()
        Thread.sleep(2000)
        lastEditItem shouldBe cartItem.item
        val savedCartItem = viewModel.allCartItems.take(1).toList().flatten()
            .single { it.item.itemId == cartItem.item.itemId }
        savedCartItem.cartItemProperties.amount shouldBe 2
        addToCartButton.assertTextContains("x 2")

    }

    @Test
    fun editInputsAreShown() {
        val name = "Milk"
        createComposable(CartItem(name), true)
        composeTestRule.onNodeWithContentDescription("Hinzufügen").assertHasClickAction()
        composeTestRule.onNodeWithContentDescription("Löschen").assertHasClickAction()
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

        composeTestRule.onNodeWithText("${CONSTANTS.CART_CHAR} x 1").assertIsDisplayed()
            .performClick()
        Thread.sleep(10000)
        composeTestRule.onNodeWithText("${CONSTANTS.CART_CHAR} x 2").assertIsDisplayed()
    }


    private fun createComposable(cartItem: CartItem, editMode: Boolean = false) {
        composeTestRule.setContent {
            NuShppingListTheme {
                ItemListElement(
                    cartItem = cartItem,
                    editMode = editMode
                )
            }
        }
    }
}
