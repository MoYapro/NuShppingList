package de.moyapro.nushppinglist.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.random.Random

@Entity
data class RecipeStep(
    @PrimaryKey(autoGenerate = true)
    var recipeStepId: Long,
    @get:JvmName("getRecipeId")
    @set:JvmName("setRecipeId")
    var recipeId: RecipeId,
    var stepNumber: Int,
    var stepDescription: String,
) {
    constructor() : this(Random.nextLong(), RecipeId(1), 1, "")
}
