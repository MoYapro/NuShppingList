package de.moyapro.nushppinglist

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import de.moyapro.nushppinglist.CONSTANTS.UNCHECKED
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
    fun addItemToCart() {
        val viewModel = createComposable(CartItem("SomeItem"))
        val itemName = "Milk"
        viewModel.add(Item(itemName))
        composeTestRule.onNodeWithText(itemName).assertDoesNotExist()
        val searchTextField =
            composeTestRule.onAllNodesWithContentDescription(EditTextField.DESCRIPTION)[0]
        searchTextField.performTextInput(itemName)
        val addButton = composeTestRule.onNodeWithText("+")
        addButton.performClick()
        composeTestRule.onNodeWithText(itemName).assertIsDisplayed()
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
//        composeTestRule.onNodeWithText(itemName).performClick()
//        val cartAfterUnChecking = viewModel.cartItems.take(1).toList().flatten()
//        assertTrue(
//            "No cartItems should be checked but was: $cartAfterUnChecking",
//            cartAfterUnChecking.none { it.checked })
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