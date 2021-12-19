package de.moyapro.nushppinglist.db.model

import androidx.room.Embedded
import androidx.room.PrimaryKey
import androidx.room.Relation

data class Recipe(
    @Embedded
    var recipeProperties    :RecipeProperties,
    @PrimaryKey
    @get:JvmName("getRecipeId")
    @set:JvmName("setRecipeId")
    var recipeId: RecipeId,
    @Relation(
        parentColumn = "recipeId",
        entityColumn = "recipeId"
    )
    var recipeSteps: List<RecipeStep> = emptyList(),
    @Relation(
        parentColumn = "recipeId",
        entityColumn = "recipeId"
    )
    var recipeItems: List<RecipeItem> = emptyList()
) {
    constructor(): this(RecipeProperties(), RecipeId(), listOf(), listOf())
}
