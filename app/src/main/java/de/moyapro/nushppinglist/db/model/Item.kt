package de.moyapro.nushppinglist.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.moyapro.nushppinglist.constants.KATEGORY
import de.moyapro.nushppinglist.constants.UNIT
import de.moyapro.nushppinglist.constants.UNIT.UNSPECIFIED
import de.moyapro.nushppinglist.db.ids.ItemId
import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP

@Entity
data class Item(
    @PrimaryKey
    @get:JvmName("getItemId")
    @set:JvmName("setItemId")
    var itemId: ItemId,
    var name: String,
    var defaultItemAmount: Int,
    var defaultItemUnit: UNIT = UNSPECIFIED,
    var price: BigDecimal,
    var kategory: KATEGORY = KATEGORY.SONSTIGES,
) {

    init {
        price = price.setScale(2, HALF_UP)
    }

    constructor(name: String, newItemId: ItemId = ItemId(), itemUnit: UNIT = UNSPECIFIED) : this(
        itemId = newItemId,
        name = name,
        defaultItemAmount = 99,
        defaultItemUnit = itemUnit,
        price = BigDecimal.ZERO.setScale(2)
    )

    constructor() : this("")
}

