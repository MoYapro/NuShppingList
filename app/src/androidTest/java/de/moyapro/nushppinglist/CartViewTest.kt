package de.moyapro.nushppinglist

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import de.moyapro.nushppinglist.constants.CONSTANTS.UNCHECKED
import de.moyapro.nushppinglist.db.model.CartDao
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.mock.CartDaoMock
import de.moyapro.nushppinglist.ui.CartView
import de.moyapro.nushppinglist.ui.EditTextField
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

internal class CartViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val cartDao: CartDao =
        CartDaoMock(CoroutineScope(TestCoroutineDispatcher() + SupervisorJob()))


    @Test
    fun cartItemIsShown() {
        val name = "Milk"
        createComposable(CartItem(name))
        composeTestRule.onNodeWithText(name).assertIsDisplayed()
        composeTestRule.onNodeWithText("UNKNOWN").assertDoesNotExist()
    }

    @Test
    fun cartItemsAreUpdated() {
        val newName = "Sugar"
        val viewModel = createComposable()
        viewModel.add(CartItem(newName))
        composeTestRule.onNodeWithText(newName).assertIsDisplayed()
    }

    @Test
    fun addNewItemToCart() = runBlocking {
        val viewModel = createComposable(CartItem("SomeItem"))
        val itemName = "Milk"
        composeTestRule.onNodeWithText(itemName).assertDoesNotExist()
        val searchTextField =
            composeTestRule.onAllNodesWithContentDescription(EditTextField.DESCRIPTION)[0]
        searchTextField.performTextInput(itemName)
        composeTestRule.onNodeWithText("+").performClick()
        composeTestRule.onNodeWithText(itemName).assertIsDisplayed()
        val updatedCart = viewModel.cartItems.take(1).toList()[0]
        assertEquals("Cart should contain two items", 2, updatedCart.size)
    }

    @Test
    fun addExistingItemToCart() = runBlocking {
        val viewModel = createComposable(CartItem("SomeItem"))
        val item = Item("Milk")
        viewModel.add(item)
        composeTestRule.onNodeWithText(item.name).assertDoesNotExist()
        val searchTextField =
            composeTestRule.onAllNodesWithContentDescription(EditTextField.DESCRIPTION)[0]
        searchTextField.performTextInput(item.name)
        composeTestRule.onNodeWithText("+").performClick()
        composeTestRule.onNodeWithText(item.name).assertIsDisplayed()
        val updatedCart = viewModel.cartItems.take(1).toList()[0]
        assertEquals("Cart should contain two items", 2, updatedCart.size)
        assertTrue(
            "Cart should contain existing item (id = ${item.itemId}) but was: $updatedCart",
            updatedCart.map { it.itemId }.contains(item.itemId)
        )
    }

    @Test
    fun getAutocompleteItem() {
        val itemName = "Milk"
        val viewModel = createComposable()
        viewModel.add(Item(itemName))
        composeTestRule.onNodeWithText(itemName).assertDoesNotExist()
        val searchTextField =
            composeTestRule.onAllNodesWithContentDescription(EditTextField.DESCRIPTION)[0]
        searchTextField.performTextInput(itemName.substring(0, 1))
        assertTrue(
            "Should find $itemName",
            viewModel.getAutocompleteItems(itemName.substring(0, 1)).contains(itemName)
        )
        composeTestRule.onNodeWithText(itemName).assertIsDisplayed()
    }

    @Test
    fun addAutocompleteItem() = runBlocking {
        val itemName = "Milk"
        val viewModel = createComposable()
        val newItem = Item(itemName)
        viewModel.add(newItem)
        composeTestRule.onNodeWithText(itemName).assertDoesNotExist()
        val searchTextField =
            composeTestRule.onAllNodesWithContentDescription(EditTextField.DESCRIPTION)[0]
        searchTextField.performTextInput(itemName.substring(0, 1))
        assertTrue(
            "Should find $itemName",
            viewModel.getAutocompleteItems(itemName.substring(0, 1)).contains(itemName)
        )
        val completedItem = composeTestRule.onNodeWithText(itemName)
        completedItem.assertIsDisplayed()
        completedItem.performClick()
        composeTestRule.onNodeWithText("+").performClick()
        val cartAfterAdding = viewModel.cartItems.take(1).toList().flatten()
        assertEquals("Should have added item to cart", newItem.itemId, cartAfterAdding[0].itemId)
    }

    @Test
    fun removeChecked() = runBlocking {
        val viewModel = createComposable(CartItem("one"), CartItem("checked one", true))
        composeTestRule.onNodeWithText("⎚").assertIsDisplayed().performClick()
        val cartItems = viewModel.cartItems.take(1).toList().flatten()
        assertEquals("Should have only one item left in cart", 1, cartItems.size)
        assertTrue("Should have removed checked items", cartItems.none { it.checked })
    }

    @Test
    fun markItemChecked() = runBlocking {
        val itemName = "Milk"
        val viewModel = createComposable(CartItem(itemName, UNCHECKED))
        composeTestRule.onNodeWithText(itemName).performClick()
        val cartAfterChecking = viewModel.cartItems.take(1).toList().flatten()
        assertTrue(
            "All cartItems should be checked but was: $cartAfterChecking",
            cartAfterChecking.all { it.checked })
        composeTestRule.onNodeWithText(itemName).performClick()
        val cartAfterUnChecking = viewModel.cartItems.take(1).toList().flatten()
        assertTrue(
            "No cartItems should be checked but was: $cartAfterUnChecking",
            cartAfterUnChecking.none { it.checked })
    }


    private fun createComposable(vararg items: CartItem): VM {
        val viewModel = VM(cartDao)
        items.forEach { viewModel.add(it) }
        composeTestRule.setContent {
            NuShppingListTheme {
                CartView(viewModel)
            }
        }
        return viewModel
    }

}
