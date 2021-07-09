package de.moyapro.nushppinglist

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import de.moyapro.nushppinglist.mock.CartDaoMock
import de.moyapro.nushppinglist.ui.EditTextField
import de.moyapro.nushppinglist.ui.ItemList
import de.moyapro.nushppinglist.ui.ItemListElement
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
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
        createComposable(Item(name))
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
        createComposable(Item(name))
        val itemNode = composeTestRule.onNodeWithText(name)
        itemNode.assertHasClickAction()
    }

    @Test
    fun switchToEditItem() {
        val name = "Milk"
        createComposable(Item(name))
        val itemNode = composeTestRule.onNodeWithText(name)
        composeTestRule.onNodeWithText("Save").assertDoesNotExist()
        itemNode.performClick()
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
    }

    @Test
    fun editInputsAreShown() {
        val name = "Milk"
        createComposable(Item(name), true)
        val itemNode = composeTestRule.onNodeWithText(name)
        composeTestRule.onAllNodesWithContentDescription(EditTextField.DESCRIPTION)
            .assertCountEquals(1)
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
        itemNode.performClick()
    }

    @Test
    fun itemIsEdited() {
        val name = "Milk"
        val textInput = "2"
        createComposable(Item(name), true)
        val editField =
            composeTestRule.onAllNodesWithContentDescription(EditTextField.DESCRIPTION)[0]
        editField.performTextInput(textInput)
        composeTestRule.onNodeWithText("Save").performClick()
        composeTestRule.onNodeWithText(name + textInput).assertIsDisplayed()
    }

    private fun createComposable(item: Item, editMode: Boolean = false) {
        composeTestRule.setContent {
            NuShppingListTheme {
                ItemListElement(item, editMode)
            }
        }
    }

    private fun createComposable(items: List<Item>) {
        val viewModel = VM(cartDao)
        items.forEach { cartDao.save(it) }
        composeTestRule.setContent {
            NuShppingListTheme {
                ItemList(viewModel)
            }
        }
    }

}