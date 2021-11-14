package de.moyapro.nushppinglist.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.moyapro.nushppinglist.db.ids.ItemId
import kotlin.random.Random

@Entity
data class Item(
    @PrimaryKey(autoGenerate = true)
    @get:JvmName("getItemId")
    @set:JvmName("setItemId")
    var itemId: ItemId,
    var name: String,
    var defaultItemAmount: Int,
    var defaultItemUnit: String
) {
    constructor(name: String, newItemId: ItemId = ItemId(Random.nextLong())) : this(newItemId, name, 1, "")
    constructor(): this("")
}

