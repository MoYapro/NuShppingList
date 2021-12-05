package de.moyapro.nushppinglist.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.random.Random

@Entity
data class RecipeProperties(
    @PrimaryKey(autoGenerate = true)
    var recipePropertiesId: Long,
    @get:JvmName("getRecipeId")
    @set:JvmName("setRecipeId")
    var recipeId: RecipeId,
    var title: String,
    var description: String,
) {
    constructor() : this(Random.nextLong(), RecipeId(1), "", "")
}
