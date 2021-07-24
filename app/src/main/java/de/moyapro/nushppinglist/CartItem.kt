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
    constructor(newItemName: String) : this(
        CartItemProperties(
            Random.nextLong(),
            0,
            Random.nextLong(),
            0,
            false
        ), Item(newItemName)
    )
}
