package de.moyapro.nushppinglist.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.moyapro.nushppinglist.db.ids.ItemId
import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP
import kotlin.random.Random

@Entity
data class Item(
    @PrimaryKey(autoGenerate = true)
    @get:JvmName("getItemId")
    @set:JvmName("setItemId")
    var itemId: ItemId,
    var name: String,
    var defaultItemAmount: Int,
    var defaultItemUnit: String,
    var price: BigDecimal,
) {

    init {
        price = price.setScale(2, HALF_UP)
    }

    constructor(name: String, newItemId: ItemId = ItemId(Random.nextLong())) : this(
        newItemId,
        name,
        99,
        "",
        BigDecimal.ZERO.setScale(2)
    )

    constructor() : this("")
}

