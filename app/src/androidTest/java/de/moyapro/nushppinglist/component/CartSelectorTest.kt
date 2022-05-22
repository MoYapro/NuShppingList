package de.moyapro.nushppinglist.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import de.moyapro.nushppinglist.constants.CONSTANTS.DEFAULT_CART
import de.moyapro.nushppinglist.db.model.Cart
import de.moyapro.nushppinglist.ui.component.CartSelector
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import de.moyapro.nushppinglist.util.DbTestHelper
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CartSelectorTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    val database = DbTestHelper.createTestDatabase()
    val viewModel = CartViewModel(database.cartDao())

    @Before
    fun setup() {
        database.clearAllTables()
    }

    @Test
    fun showSelectedCart(): Unit = runBlocking {
        val numbers = 0..10
        viewModel.add(DEFAULT_CART)
        repeat(numbers.last + 1) { i ->
            viewModel.add(Cart("cart $i"))
        }
        Thread.sleep(100)
        composeTestRule.setContent {
            NuShppingListTheme {
                CartSelector(viewModel)
            }
        }
        composeTestRule.onNodeWithText(DEFAULT_CART.cartName).assertIsDisplayed()
        composeTestRule.onNodeWithText(DEFAULT_CART.cartName).performClick()

        repeat(numbers.count()) { number ->
            val currentCartName = "cart $number"
            composeTestRule.onNodeWithText(currentCartName).performClick() // select cart
            Thread.sleep(100)
            with(viewModel.selectedCart.take(1).toList().single()) {
                this?.cartName shouldBe currentCartName
                this?.selected shouldBe true
            }
            viewModel.allCart.take(1).toList().flatten().filter { it.cartName != currentCartName }
                .none { it.selected }
            composeTestRule.onNodeWithText(currentCartName)
                .performClick() // open selection list for next cart
        }
    }
}
