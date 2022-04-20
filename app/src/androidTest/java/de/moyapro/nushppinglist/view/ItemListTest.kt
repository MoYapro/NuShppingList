package de.moyapro.nushppinglist.view

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.model.Cart
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.ui.ItemList
import de.moyapro.nushppinglist.ui.component.EditTextField
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import de.moyapro.nushppinglist.util.DbTestHelper
import de.moyapro.nushppinglist.util.assertDoesNotExist
import de.moyapro.nushppinglist.util.assertIsDisplayed
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

internal class ItemListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    val database = DbTestHelper.createTestDatabase()

    private val cartDao: CartDao = database.cartDao()

    @Before
    fun setup() {
        database.clearAllTables()
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
    fun addNewItemByName() {
        val newItemName = "some new item"
        createComposable(emptyList())
        val input = composeTestRule.onNodeWithContentDescription(EditTextField.DESCRIPTION)
        input.performTextInput(newItemName)
        val addNewItemButton = composeTestRule.onNodeWithContentDescription("Neu")
        addNewItemButton.performClick()


    }

    @Test
    fun filterInput() {
        val filter = "Apple"
        val otherItemName = "Milk"
        val names = listOf(otherItemName, filter)
        createComposable(names.map { Item(it) })
        val input = composeTestRule.onNodeWithContentDescription(EditTextField.DESCRIPTION)
        input.performTextInput(filter)
        composeTestRule.onNodeWithText(otherItemName).assertDoesNotExist()
        composeTestRule.onAllNodesWithText(filter).assertCountEquals(2)
    }

    @Test
    fun cleanFilterInput() {
        val names = listOf("Milk", "Apple")
        val filterText = "some filter text"
        createComposable(names.map { Item(it) })
        val input = composeTestRule.onNodeWithContentDescription(EditTextField.DESCRIPTION)
        input.performTextInput(filterText)
        composeTestRule.onNodeWithContentDescription("Leeren").performClick()
        composeTestRule.onNodeWithText(filterText).assertDoesNotExist()
        composeTestRule.onNodeWithText(names.random()).assertIsDisplayed()
    }

    @Test
    fun displayItemsInCart() {
        val cart1 = Cart("cart1")
        val cart2 = Cart("cart2")
        val carts = listOf(cart1, cart2)
        val cartItem1 = CartItem("item1").apply {
            cartItemProperties.inCart = cart1.cartId; cartItemProperties.amount = 101
        }
        val cartItem2 = CartItem("item2").apply {
            cartItemProperties.inCart = cart2.cartId; cartItemProperties.amount = 202
        }
        val cartItem3 = CartItem("item3").apply {
            cartItemProperties.inCart = null; cartItemProperties.amount = 303
        }
        createComposable(carts = carts, cartItems = listOf(cartItem1, cartItem2, cartItem3))
        listOf("item1", "item2", "item3", "101", "202", "303").assertIsDisplayed(composeTestRule)

        composeTestRule.onNodeWithText("Alle Listen").performClick()
        composeTestRule.onNodeWithText("cart1").performClick()
        Thread.sleep(100)
        listOf("item1", "item2", "item3", "101").assertIsDisplayed(composeTestRule)
        Thread.sleep(3000)
        listOf("202", "303").assertDoesNotExist(composeTestRule)
        Thread.sleep(2000)
        composeTestRule.onNodeWithText("cart1").performClick()
        composeTestRule.onNodeWithText("cart2").performClick()
        Thread.sleep(100)
        listOf("item1", "item2", "item3", "202").assertIsDisplayed(composeTestRule)
        Thread.sleep(3000)
        listOf("101", "303").assertDoesNotExist(composeTestRule)

    }

    private fun createComposable(
        items: List<Item> = emptyList(),
        cartItems: List<CartItem> = emptyList(),
        carts: List<Cart> = emptyList(),
    ) = runBlocking {
        val viewModel = CartViewModel(cartDao)
        carts.forEach { cartDao.save(it) }
        items.forEach { cartDao.save(it) }
        cartItems.forEach {
            cartDao.save(it.item)
            cartDao.save(it.cartItemProperties)
        }
        Thread.sleep(100)
        composeTestRule.setContent {
            NuShppingListTheme {
                ItemList(viewModel)
            }
        }
    }

}
