package de.moyapro.nushppinglist.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.moyapro.nushppinglist.db.ids.ItemId
import java.util.*

@Entity
data class CartItemProperties(
    @PrimaryKey
    var cartItemPropertiesId: UUID = UUID.randomUUID(),
    var cartItemId: UUID,
    @get:JvmName("getItemId")
    @set:JvmName("setItemId")
    var itemId: ItemId,
    @get:JvmName("getRecipeId")
    @set:JvmName("setRecipeId")
    var recipeId: RecipeId?,
    var amount: Int,
    var checked: Boolean,
) {
    constructor(newItemId: ItemId, recipeId: RecipeId, amount: Int = 1) : this(
        cartItemPropertiesId = UUID.randomUUID(),
        cartItemId = newItemId.id,
        itemId = newItemId,
        recipeId = recipeId,
        amount = amount,
        checked = false
    )

    constructor(newItemId: ItemId, amount: Int = 1) : this(
        newItemId,
        RecipeId(),
    )

    constructor() : this(ItemId())
}
