package de.moyapro.nushppinglist.util

import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.CartItemProperties
import de.moyapro.nushppinglist.db.model.Item
import io.kotest.matchers.shouldBe
import org.junit.Test

class SortCartItemPairByCheckedAndNameTest {

    val itemA = Item("aaa")
    val itemB = Item("bbb")

    @Test
    fun byName() {
        val cartItem1 =
            CartItem(CartItemProperties(itemA.itemId, CONSTANTS.DEFAULT_CART.cartId), itemA)
        val cartItem2 =
            CartItem(CartItemProperties(itemB.itemId, CONSTANTS.DEFAULT_CART.cartId), itemB)
        SortCartItemPairByCheckedAndName.compare(cartItem1, cartItem2) shouldBe -1
        SortCartItemPairByCheckedAndName.compare(cartItem2, cartItem1) shouldBe 1
        SortCartItemPairByCheckedAndName.compare(cartItem1, cartItem1) shouldBe 0
        SortCartItemPairByCheckedAndName.compare(cartItem2, cartItem2) shouldBe 0
    }

    @Test
    fun cartItemsFirst() {
        val cartItem1 =
            CartItem(CartItemProperties(itemA.itemId, CONSTANTS.DEFAULT_CART.cartId, amount = 0),
                itemA)
        val cartItem2 =
            CartItem(CartItemProperties(itemB.itemId, CONSTANTS.DEFAULT_CART.cartId, amount = 1),
                itemB)
        SortCartItemPairByCheckedAndName.compare(cartItem1, cartItem2) shouldBe 1
        SortCartItemPairByCheckedAndName.compare(cartItem2, cartItem1) shouldBe -1
        SortCartItemPairByCheckedAndName.compare(cartItem1, cartItem1) shouldBe 0
        SortCartItemPairByCheckedAndName.compare(cartItem2, cartItem2) shouldBe 0
    }

    @Test
    fun checkedLast() {
        val cartItem1 = CartItem(CartItemProperties(itemA.itemId,
            CONSTANTS.DEFAULT_CART.cartId,
            amount = 1).apply { checked = false }, itemA)
        val cartItem2 = CartItem(CartItemProperties(itemB.itemId,
            CONSTANTS.DEFAULT_CART.cartId,
            amount = 1).apply { checked = true }, itemB)
        SortCartItemPairByCheckedAndName.compare(cartItem1, cartItem2) shouldBe -1
        SortCartItemPairByCheckedAndName.compare(cartItem2, cartItem1) shouldBe 1
        SortCartItemPairByCheckedAndName.compare(cartItem1, cartItem1) shouldBe 0
        SortCartItemPairByCheckedAndName.compare(cartItem2, cartItem2) shouldBe 0
    }
}
