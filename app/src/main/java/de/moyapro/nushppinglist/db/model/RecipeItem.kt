package de.moyapro.nushppinglist.db.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecipeItem(
    @PrimaryKey(autoGenerate = true)
    var recipeItemId: Long,
    var recipeId: Long,
    var amount: Double,
    @Embedded
    var item: Item,
)

