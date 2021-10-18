package de.moyapro.nushppinglist.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecipeStep(
    @PrimaryKey(autoGenerate = true)
    var recipeStepId: Long,
    var recipeId: Long,
    var stepNumber: Int,
    var stepDescription: String,
)
