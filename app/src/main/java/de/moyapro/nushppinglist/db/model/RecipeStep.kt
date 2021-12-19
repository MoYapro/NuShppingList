package de.moyapro.nushppinglist.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class RecipeStep(
    @PrimaryKey
    var recipeStepId: UUID = UUID.randomUUID(),
    @get:JvmName("getRecipeId")
    @set:JvmName("setRecipeId")
    var recipeId: RecipeId,
    var stepNumber: Int,
    var stepDescription: String,
) {
    constructor() : this(recipeId = RecipeId(), stepNumber = 1, stepDescription = "")
}
