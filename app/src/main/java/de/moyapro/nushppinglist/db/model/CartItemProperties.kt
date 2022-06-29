package de.moyapro.nushppinglist.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.db.ids.CartId
import de.moyapro.nushppinglist.db.ids.ItemId
import java.util.*

@Entity
data class CartItemProperties(
    @PrimaryKey
    var cartItemPropertiesId: UUID = UUID.randomUUID(),
    var cartItemId: UUID,
    @get:JvmName("getInCart")
    @set:JvmName("setInCart")
    @ColumnInfo(name = "inCart", typeAffinity = ColumnInfo.BLOB, defaultValue = "0")
    var inCart: CartId = CONSTANTS.DEFAULT_CART.cartId,
    @get:JvmName("getItemId")
    @set:JvmName("setItemId")
    var itemId: ItemId,
    @get:JvmName("getRecipeId")
    @set:JvmName("setRecipeId")
    var recipeId: RecipeId?,
    var amount: Int,
    var checked: Boolean,
) {
    constructor(newItemId: ItemId, recipeId: RecipeId, amount: Int = 1, inCart: CartId) : this(
        cartItemPropertiesId = UUID.randomUUID(),
        cartItemId = newItemId.id,
        itemId = newItemId,
        recipeId = recipeId,
        amount = amount,
        inCart = inCart,
        checked = false
    )

    constructor(newItemId: ItemId, inCart: CartId, amount: Int = 1) : this(
        newItemId = newItemId,
        recipeId = RecipeId(),
        amount = amount,
        inCart = inCart,
    )

    constructor(inCart: CartId) : this(ItemId(), inCart)
    constructor() : this(ItemId(),   CONSTANTS.DEFAULT_CART.cartId)
}
