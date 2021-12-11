package de.moyapro.nushppinglist

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.mock.CartDaoMock
import de.moyapro.nushppinglist.ui.EditTextField
import de.moyapro.nushppinglist.ui.ItemList
import de.moyapro.nushppinglist.ui.ItemListElement
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Rule
import org.junit.Test

internal class ItemListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val cartDao: CartDao =
        CartDaoMock(CoroutineScope(TestCoroutineDispatcher() + SupervisorJob()))

    @Test
    fun showItemName() {
        val name = "Milk"
        createComposable(CartItem(name))
        composeTestRule.onNodeWithText(name).assertIsDisplayed()
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
    fun clickableItemList() {
        val name = "Milk"
        createComposable(CartItem(name))
        val itemNode = composeTestRule.onNodeWithText(name)
        itemNode.assertHasClickAction()
    }

    @Test
    fun switchToEditItem() {
        val name = "Milk"
        createComposable(CartItem(name))
        val itemNode = composeTestRule.onNodeWithText(name)
        composeTestRule.onNodeWithText("Save").assertDoesNotExist()
        itemNode.performClick()
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
    }

    @Test
    fun editInputsAreShown() {
        val name = "Milk"
        createComposable(CartItem(name), true)
        composeTestRule.onAllNodesWithContentDescription(EditTextField.DESCRIPTION)
            .assertCountEquals(1)
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
        composeTestRule.onNodeWithText(name).assertIsDisplayed()
    }


    private fun createComposable(cartItem: CartItem, editMode: Boolean = false) {
        composeTestRule.setContent {
            NuShppingListTheme {
                ItemListElement(cartItem = cartItem, editMode = editMode)
            }
        }
    }

    private fun createComposable(items: List<Item>) = runBlocking {
        val viewModel = CartViewModel(cartDao)
        items.forEach { cartDao.save(it) }
        composeTestRule.setContent {
            NuShppingListTheme {
                ItemList(viewModel)
            }
        }
    }

}
