package de.moyapro.nushppinglist

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.random.Random

@Entity
data class Item(
    @PrimaryKey val id: Long,
    val name: String
) {
    constructor(name: String) : this(Random.nextLong(), name)
}

