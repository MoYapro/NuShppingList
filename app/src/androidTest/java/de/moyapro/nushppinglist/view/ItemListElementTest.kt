package de.moyapro.nushppinglist.view

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import de.moyapro.nushppinglist.constants.CONSTANTS.DEFAULT_CART
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.ui.ItemList
import de.moyapro.nushppinglist.ui.ItemListElement
import de.moyapro.nushppinglist.ui.component.EditTextField
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import de.moyapro.nushppinglist.util.DbTestHelper
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
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
                    cartItem = CartItem("Milk", DEFAULT_CART.cartId),
                    saveAction = saveAction,
                    editMode = true,
                )
            }
        }
        val editField =
            composeTestRule.onAllNodesWithContentDescription(EditTextField.DESCRIPTION)[0]
        editField.performClick()
        editField.performTextInput("2")
        composeTestRule.onAllNodesWithContentDescription("Hinzufügen")[1].assertHasClickAction()
            .performClick()
        assertTrue("Saveaction should be called", saveActionCalled)
        assertEquals("Itemname should be updated", "Milk2", itemText)
    }

    @Test
    fun addItemToCart() {
        var addedItem: Item? = null
        val existingItem = CartItem("newItem", DEFAULT_CART.cartId)
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
        val cartItem = CartItem("thing", DEFAULT_CART.cartId)
        val viewModel = CartViewModel(DbTestHelper.createTestDatabase().cartDao())
        var lastEditItem: Item? = null
        viewModel.add(DEFAULT_CART)
        viewModel.selectCart(DEFAULT_CART)
        viewModel.add(cartItem)
        Thread.sleep(100)
        viewModel.allCartItems.take(1).toList().flatten() shouldHaveSize 1

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
        val addToCartButton = composeTestRule.onAllNodesWithContentDescription("Hinzufügen")[0]
        addToCartButton.assertTextContains("x 1")
        addToCartButton.performClick()
        Thread.sleep(100)
        lastEditItem shouldBe cartItem.item
        val savedItems = viewModel.allCartItems.take(1).toList().flatten()
        savedItems shouldHaveSize 1
        val savedCartItem = savedItems
            .single { it.item.itemId == cartItem.item.itemId }
        savedCartItem.cartItemProperties.amount shouldBe 2
    }

    @Test
    fun editInputsAreShown() {
        val name = "Milk"
        createComposable(CartItem(name, DEFAULT_CART.cartId), true)
        composeTestRule.onAllNodesWithContentDescription("Hinzufügen").assertAll(hasClickAction())
        composeTestRule.onAllNodesWithContentDescription("Löschen").assertAll(hasClickAction())
        composeTestRule.onAllNodesWithText(name).assertCountEquals(2)
    }

    @Test
    fun increaseAmountOnAddAgain() {
        val cartItem = CartItem("thing", DEFAULT_CART.cartId)
        val viewModel = CartViewModel(DbTestHelper.createTestDatabase().cartDao())
        viewModel.add(DEFAULT_CART)
        viewModel.add(cartItem)

        composeTestRule.setContent {
            NuShppingListTheme {
                ItemList(viewModel)
            }
        }

        Thread.sleep(100)
        composeTestRule.onNodeWithText("x 1").assertIsDisplayed()
            .performClick()
        Thread.sleep(100)
        composeTestRule.onNodeWithText("x 2").assertIsDisplayed()
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
