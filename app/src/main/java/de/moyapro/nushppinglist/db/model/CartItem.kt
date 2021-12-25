package de.moyapro.nushppinglist.db.model

import androidx.room.Embedded
import androidx.room.Relation
import de.moyapro.nushppinglist.db.ids.ItemId

data class CartItem(
    @Embedded
    val cartItemProperties: CartItemProperties,
    @Relation(
        parentColumn = "itemId",
        entityColumn = "itemId"
    )
    val item: Item,
) {
    constructor(
        newItemName: String,
        checked: Boolean = false,
        newItemId: ItemId = ItemId(),
    ) : this(
        cartItemProperties = CartItemProperties(
            cartItemId = newItemId.id,
            itemId = newItemId,
            recipeId = null,
            amount = 1,
            checked = checked
        ),
        item = Item(newItemName, newItemId)
    )

    constructor(item: Item) : this(
        CartItemProperties(
            cartItemId = item.itemId.id,
            itemId = item.itemId,
            recipeId = RecipeId(),
            amount = item.defaultItemAmount,
            checked = false),
        item
    )

    init {
        require(cartItemProperties.itemId == item.itemId) { "ItemId must match in Item and CartItemsProperties" }
    }
}
