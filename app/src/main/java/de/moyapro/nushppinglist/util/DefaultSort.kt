package de.moyapro.nushppinglist.util

import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.RecipeId

object CartItemByCheckedAndName : Comparator<CartItem> {
    override fun compare(p0: CartItem, p1: CartItem): Int {
        return p0.item.name.compareTo(p1.item.name)
    }
}

object SortCartItemPairByName : Comparator<Pair<RecipeId?, CartItem>> {

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
