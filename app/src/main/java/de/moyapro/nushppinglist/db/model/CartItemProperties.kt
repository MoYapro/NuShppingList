package de.moyapro.nushppinglist.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.moyapro.nushppinglist.db.ids.ItemId
import kotlin.random.Random

@Entity
data class CartItemProperties(
    @PrimaryKey(autoGenerate = true)
    var cartItemPropertiesId: Long,
    var cartItemId: Long,
    @get:JvmName("getItemId")
    @set:JvmName("setItemId")
    var itemId: ItemId,
    var amount: Int,
    var checked: Boolean
) {
    constructor(newItemId: ItemId, amount: Int = 1) : this(
        cartItemPropertiesId = newItemId.id,
        cartItemId = newItemId.id,
        itemId = newItemId,
        amount = amount,
        checked = false
    )

    constructor() : this(ItemId(Random.nextLong()))
}
