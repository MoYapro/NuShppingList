package de.moyapro.nushppinglist.ui.component

import de.moyapro.nushppinglist.db.model.CartItemProperties
import de.moyapro.nushppinglist.db.model.RecipeItem

fun amountText(recipeItem: RecipeItem): String = "${recipeItem.amount} ${recipeItem.item.defaultItemUnit}"

fun amountText(cartItemProperties: CartItemProperties?): String {
    if (null == cartItemProperties || 0 == cartItemProperties.amount) {
        return ""
    }
    return "x ${cartItemProperties.amount}"
}
