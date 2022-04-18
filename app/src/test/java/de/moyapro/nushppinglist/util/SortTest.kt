package de.moyapro.nushppinglist.util

import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.RecipeId
import de.moyapro.nushppinglist.ui.util.createSampleCartItem
import io.kotest.matchers.collections.shouldContainInOrder
import org.junit.Test

class SortTest {

    @Test
    fun sortCart() {
        val cartItems: List<CartItem> = listOf(
            createSampleCartItem().apply { item.name = "A"; cartItemProperties.checked = true },
            createSampleCartItem().apply { item.name = "B"; cartItemProperties.checked = true },
            createSampleCartItem().apply { item.name = "D"; cartItemProperties.checked = false },
            createSampleCartItem().apply { item.name = "C"; cartItemProperties.checked = false },
        )
        val sorted = cartItems.sortedWith(CartItemByName)
        sorted.map { it.item.name } shouldContainInOrder listOf("A", "B", "C", "D")
    }
    @Test
    fun sortCartPairs() {
        val cartItems: List<Pair<RecipeId?, CartItem>> = listOf(
            Pair(RecipeId(), createSampleCartItem().apply { item.name = "A"; cartItemProperties.checked = true }),
            Pair(null, createSampleCartItem().apply { item.name = "B"; cartItemProperties.checked = true }),
            Pair(RecipeId(), createSampleCartItem().apply { item.name = "D"; cartItemProperties.checked = false }),
            Pair(RecipeId(), createSampleCartItem().apply { item.name = "C"; cartItemProperties.checked = false }),
        )
        val sorted = cartItems.sortedWith(SortCartItemPairByCheckedAndNameRecipe)
        sorted.map { it.second.item.name } shouldContainInOrder listOf("C", "D", "A", "B")
    }
}
