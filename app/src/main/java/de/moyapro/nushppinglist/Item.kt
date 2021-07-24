package de.moyapro.nushppinglist

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.random.Random

@Entity
data class Item(
    @PrimaryKey(autoGenerate = true)
    val itemId: Long,
    val name: String
) {
    constructor(name: String, newItemId: Long = Random.nextLong()) : this(newItemId, name)
}

