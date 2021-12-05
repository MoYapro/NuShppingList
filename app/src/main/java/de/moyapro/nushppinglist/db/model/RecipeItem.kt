package de.moyapro.nushppinglist.db.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.random.Random

@Entity
data class RecipeItem(
    @PrimaryKey(autoGenerate = true)
    var recipeItemId: Long,
    @get:JvmName("getRecipeId")
    @set:JvmName("setRecipeId")
    var recipeId: RecipeId,
    var amount: Double,
    @Embedded
    var item: Item,
) {
    constructor() : this(Random.nextLong(), RecipeId(1), 1.0, Item())
}

