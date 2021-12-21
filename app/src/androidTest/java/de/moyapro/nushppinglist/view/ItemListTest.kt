package de.moyapro.nushppinglist.view

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.mock.CartDaoMock
import de.moyapro.nushppinglist.ui.ItemList
import de.moyapro.nushppinglist.ui.component.EditTextField
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
    fun showItemList() {
        val names = listOf("Milk", "Apple")
        createComposable(names.map { Item(it) })
        names.forEach { name ->
            composeTestRule.onNodeWithText(name).assertIsDisplayed()
        }
    }

    @Test
    fun filterInput() {
        val filter = "Apple"
        val otherItemName = "Milk"
        val names = listOf(otherItemName, filter)
        createComposable(names.map { Item(it) })
        val input = composeTestRule.onAllNodesWithContentDescription(EditTextField.DESCRIPTION)[0]
        input.performTextInput(filter)
        composeTestRule.onNodeWithText(otherItemName).assertDoesNotExist()
        composeTestRule.onAllNodesWithText(filter).assertCountEquals(2)
    }

    @Test
    fun cleanFilterInput() {
        val names = listOf("Milk", "Apple")
        val filterText = "some filter text"
        createComposable(names.map { Item(it) })
        val input = composeTestRule.onAllNodesWithContentDescription(EditTextField.DESCRIPTION)[0]
        input.performTextInput(filterText)
        composeTestRule.onNodeWithContentDescription("Leeren").performClick()
        composeTestRule.onNodeWithText(filterText).assertDoesNotExist()
        composeTestRule.onNodeWithText(names.random()).assertIsDisplayed()
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
