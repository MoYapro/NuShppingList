package de.moyapro.nushppinglist.db.model

import androidx.room.Embedded
import androidx.room.PrimaryKey
import androidx.room.Relation

data class Recipe(
    @Embedded
    val recipeProperties    :RecipeProperties,
    @PrimaryKey(autoGenerate = true)
    var recipeId: Long,
    @Relation(
        parentColumn = "recipeId",
        entityColumn = "recipeId"
    )
    val recipeSteps: List<RecipeStep> = emptyList(),
    @Relation(
        parentColumn = "recipeId",
        entityColumn = "recipeId"
    )
    val recipeItems: List<RecipeItem> = emptyList()
) {
}
