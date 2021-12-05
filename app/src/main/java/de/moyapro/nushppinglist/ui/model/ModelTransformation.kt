package de.moyapro.nushppinglist.ui.model

import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.RecipeId

object ModelTransformation {

    fun groupCartItemsByRecipe(cartItems: List<CartItem>): Map<RecipeId?, List<CartItem>> {
        val associateBy = cartItems.groupBy {
            val dbValue = it.cartItemProperties.recipeId.recipeId
            if (-1L == dbValue) {
                null
            } else {
                RecipeId(dbValue)
            }
        }
        return associateBy
    }
}
