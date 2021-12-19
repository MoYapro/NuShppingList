package de.moyapro.nushppinglist.ui.model

import de.moyapro.nushppinglist.db.model.CartItem
import de.moyapro.nushppinglist.db.model.RecipeId

object ModelTransformation {

    fun groupCartItemsByRecipe(cartItems: List<CartItem>): Map<RecipeId?, List<CartItem>> {
        val associateBy = cartItems.groupBy {
            it.cartItemProperties.recipeId
        }
        return associateBy
    }
}
