package de.moyapro.nushppinglist.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class RecipeProperties(
    @PrimaryKey
    var recipePropertiesId: UUID = UUID.randomUUID(),
    @get:JvmName("getRecipeId")
    @set:JvmName("setRecipeId")
    var recipeId: RecipeId,
    var title: String,
    var description: String,
) {
    constructor() : this(recipeId = RecipeId(), title = "", description = "")
}
