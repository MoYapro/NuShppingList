package de.moyapro.nushppinglist

import androidx.room.Embedded
import androidx.room.Relation
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
        newItemId: Long = Random.nextLong()
    ) : this(
        CartItemProperties(
            newItemId,
            0,
            newItemId,
            0,
            checked
        ), Item(newItemName, newItemId)
    )

    init {
        require(cartItemProperties.itemId == item.itemId) { "ItemId must match in Item and CartItemsProperties" }
    }
}
