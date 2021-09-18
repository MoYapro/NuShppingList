package de.moyapro.nushppinglist.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.random.Random

@Entity
data class CartItemProperties(
    @PrimaryKey(autoGenerate = true)
    val cartItemPropertiesId: Long,
    val cartItemId: Long,
    val itemId: Long,
    val amount: Int,
    val checked: Boolean
) {
    constructor(newItemId: Long = Random.nextLong(), amount: Int = 1) : this(
        cartItemPropertiesId = newItemId,
        cartItemId = newItemId,
        itemId = newItemId,
        amount = amount,
        checked = false
    )
}
