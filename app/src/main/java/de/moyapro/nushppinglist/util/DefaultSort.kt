package de.moyapro.nushppinglist.util

import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.RecipeId

object CartItemByName : Comparator<CartItem> {
    override fun compare(p0: CartItem, p1: CartItem): Int {
        return p0.item.name.compareTo(p1.item.name)
    }
}

object SortCartItemPairByCheckedAndNameRecipe : Comparator<Pair<RecipeId?, CartItem>> {

    override fun compare(p0: Pair<RecipeId?, CartItem>, p1: Pair<RecipeId?, CartItem>): Int {
        val compareChecked =
            p0.second.cartItemProperties.checked.compareTo(p1.second.cartItemProperties.checked)
        return if (0 == compareChecked) {
            p0.second.item.name.compareTo(p1.second.item.name)
        } else {
            compareChecked
        }
    }
}

object SortCartItemPairByCheckedAndName : Comparator<CartItem> {

    override fun compare(p0: CartItem, p1: CartItem): Int {
        val p0InList = p0.cartItemProperties.amount > 0
        val p1InList = p1.cartItemProperties.amount > 0
        val compareInList = p0InList.compareTo(p1InList)
        val compareChecked = p0.cartItemProperties.checked.compareTo(p1.cartItemProperties.checked)

        return when {
            0 != compareInList -> compareInList * -1
            0 != compareChecked -> compareChecked
            else -> p0.item.name.compareTo(p1.item.name)
        }

    }
}
