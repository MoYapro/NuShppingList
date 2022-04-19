package de.moyapro.nushppinglist.view

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import de.moyapro.nushppinglist.constants.CONSTANTS.UNCHECKED
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.ui.CartView
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import de.moyapro.nushppinglist.util.DbTestHelper
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

internal class CartViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val cartDao = DbTestHelper.createTestDatabase().cartDao()


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
        Thread.sleep(100)
        composeTestRule.onNodeWithText(newName).assertIsDisplayed()
    }

    @Test
    fun removeChecked(): Unit = runBlocking {
        val viewModel = createComposable(CartItem("one"), CartItem("checked one", true))
        composeTestRule.onNodeWithText("⎚").assertIsDisplayed().performClick()
        Thread.sleep(100)
        val cartItems = viewModel.cartItems.take(1).toList().flatten()
        assertEquals("Should have only one item left in cart", 1, cartItems.size)
        assertTrue("Should have removed checked items", cartItems.none { it.checked })
    }

    @Test
    fun markItemChecked_Unchecked(): Unit = runBlocking {
        val itemName = "Milk"
        val viewModel = createComposable(CartItem(itemName, UNCHECKED))
        composeTestRule.onNodeWithText(itemName).performClick()
        Thread.sleep(100)
        val cartAfterChecking = viewModel.cartItems.take(1).toList().flatten()
        assertTrue(
            "All cartItems should be checked but was: $cartAfterChecking",
            cartAfterChecking.all { it.checked })
        composeTestRule.onNodeWithText(itemName).performClick()
        Thread.sleep(100)
        val cartAfterUnChecking = viewModel.cartItems.take(1).toList().flatten()
        assertTrue(
            "No cartItems should be unchecked but was: $cartAfterUnChecking",
            cartAfterUnChecking.none { it.checked })
    }


    private fun createComposable(vararg items: CartItem): CartViewModel {
        val viewModel = CartViewModel(cartDao)
        items.forEach { viewModel.add(it) }
        composeTestRule.setContent {
            NuShppingListTheme {
                CartView(viewModel)
            }
        }
        return viewModel
    }

}
