package de.moyapro.nushppinglist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CartItemProperties(
    @PrimaryKey(autoGenerate = true)
    val cartItemPropertiesId: Long,
    val cartItemId: Long,
    val itemId: Long,
    val amount: Int,
    val checked: Boolean
)
