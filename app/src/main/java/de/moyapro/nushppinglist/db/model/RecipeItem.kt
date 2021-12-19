package de.moyapro.nushppinglist.db.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class RecipeItem(
    @PrimaryKey
    var recipeItemId: UUID = UUID.randomUUID(),
    @get:JvmName("getRecipeId")
    @set:JvmName("setRecipeId")
    var recipeId: RecipeId,
    var amount: Double,
    @Embedded
    var item: Item,
) {
    constructor() : this(recipeId = RecipeId(), amount = 1.0, item = Item())
}

