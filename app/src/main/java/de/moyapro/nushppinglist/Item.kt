package de.moyapro.nushppinglist

import kotlin.random.Random

data class Item(
    val id: Long,
    val name: String
) {
    constructor(name: String) : this(Random.nextLong(), name)
}

