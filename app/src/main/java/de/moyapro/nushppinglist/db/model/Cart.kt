package de.moyapro.nushppinglist.db.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import de.moyapro.nushppinglist.db.ids.CartId

@Entity(indices = [Index(value = ["cartName"], unique = true)])
data class Cart(
    @PrimaryKey
    @get:JvmName("getCartId")
    @set:JvmName("setCartId")
    var cartId: CartId = CartId(),
    var cartName: String,
    var synced: Boolean = false,
    var selected: Boolean = false,
) {
    constructor() : this(cartName = "")
}
