package de.moyapro.nushppinglist.db.model

import androidx.room.Embedded
import androidx.room.Relation
import de.moyapro.nushppinglist.db.ids.ItemId
import kotlin.random.Random

data class CartItem(
    @Embedded
    val cartItemProperties: CartItemProperties,
    @Relation(
        parentColumn = "itemId",
        entityColumn = "itemId"
    )
    val item: Item
) {
    constructor(
        newItemName: String,
        checked: Boolean = false,
        newItemId: ItemId = ItemId(Random.nextLong())
    ) : this(
        CartItemProperties(
            newItemId.id,
            newItemId.id,
            newItemId,
            1,
            checked
        ), Item(newItemId, newItemName)
    )

    constructor(item: Item) : this(
        CartItemProperties(item.itemId.id, item.itemId.id, item.itemId, 1, false),
        item
    )

    init {
        require(cartItemProperties.itemId == item.itemId) { "ItemId must match in Item and CartItemsProperties" }
    }
}
