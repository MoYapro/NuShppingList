package de.moyapro.nushppinglist.view

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import de.moyapro.nushppinglist.constants.CONSTANTS.DEFAULT_CART
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
    private lateinit var viewModel: CartViewModel


    @Before
    fun setup() {
        viewModel = CartViewModel(cartDao)
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
    fun addNewItemByName_noCartSelected() {
        val newItemName = "some new item"
        createComposable(carts = listOf(DEFAULT_CART))
        val input = composeTestRule.onNodeWithContentDescription(EditTextField.DESCRIPTION)
        input.performTextInput(newItemName)
        val addNewItemButton = composeTestRule.onNodeWithContentDescription("Neu")
        addNewItemButton.performClick()
    }

    @Test
    fun addNewItemByName_selectedCart() {
        val cart1 = Cart("cart1")
        val cart2 = Cart("cart2")
        val carts = listOf(cart1, cart2)
        viewModel.add(DEFAULT_CART)
        Thread.sleep(100)
        val itemsPerCart: MutableMap<Cart, MutableList<String>> = mutableMapOf()
        itemsPerCart[cart1] = mutableListOf()
        itemsPerCart[cart2] = mutableListOf()
        createComposable(carts = carts)
        composeTestRule.onNodeWithText(DEFAULT_CART.cartName).performClick()
        repeat(6) { i ->
            val cartToUse = carts[i % 2]
            val itemName = "${cartToUse.cartName}-item$i"
            itemsPerCart[cartToUse]?.add(itemName)
            composeTestRule.onNodeWithText(cartToUse.cartName).performClick()
            val input = composeTestRule.onNodeWithContentDescription(EditTextField.DESCRIPTION)
            input.performTextInput(itemName)
            composeTestRule.onNodeWithContentDescription("Neu").performClick()
            composeTestRule.onNodeWithContentDescription("Leeren").performClick()
            itemsPerCart[cartToUse]?.assertIsDisplayed(composeTestRule)
            composeTestRule.onNodeWithText(cartToUse.cartName).performClick() // open cart selector
        }

        listOf("cart1-item0",
            "cart1-item2",
            "cart1-item4",
            "cart2-item1",
            "cart2-item3",
            "cart2-item5").assertIsDisplayed(composeTestRule)
        listOf("cart1-item1",
            "cart1-item3",
            "cart1-item5",
            "cart2-item0",
            "cart2-item2",
            "cart2-item4").assertDoesNotExist(composeTestRule)
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
        val carts = listOf(DEFAULT_CART, cart1, cart2)
        val cartItem1 = CartItem("item1", cart1.cartId).apply {
            cartItemProperties.amount = 101
        }
        val cartItem2 = CartItem("item2", cart2.cartId).apply {
            cartItemProperties.amount = 202
        }
        val cartItem3 = CartItem("item3", DEFAULT_CART.cartId).apply {
            cartItemProperties.amount = 303
        }
        createComposable(carts = carts, cartItems = listOf(cartItem1, cartItem2, cartItem3))
        listOf("item1", "item2", "item3", "303").assertIsDisplayed(composeTestRule)
        listOf("101", "202").assertDoesNotExist(composeTestRule)

        composeTestRule.onNodeWithText(DEFAULT_CART.cartName).performClick()
        composeTestRule.onNodeWithText("cart1").performClick()
        Thread.sleep(100)
        listOf("item1", "item2", "item3", "101").assertIsDisplayed(composeTestRule)
        Thread.sleep(100)
        listOf("202", "303").assertDoesNotExist(composeTestRule)
        Thread.sleep(100)
        composeTestRule.onNodeWithText("cart1").performClick()
        composeTestRule.onNodeWithText("cart2").performClick()
        Thread.sleep(100)
        listOf("item1", "item2", "item3", "202").assertIsDisplayed(composeTestRule)
        listOf("101", "303").assertDoesNotExist(composeTestRule)
    }

    private fun createComposable(
        items: List<Item> = emptyList(),
        cartItems: List<CartItem> = emptyList(),
        carts: List<Cart> = emptyList(),
    ) = runBlocking {
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
