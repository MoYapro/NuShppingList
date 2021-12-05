package de.moyapro.nushppinglist.db.model

import androidx.room.Embedded
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlin.random.Random

data class Recipe(
    @Embedded
    var recipeProperties    :RecipeProperties,
    @PrimaryKey(autoGenerate = true)
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
    constructor(): this(RecipeProperties(), RecipeId(Random.nextLong()), listOf(), listOf())
}
