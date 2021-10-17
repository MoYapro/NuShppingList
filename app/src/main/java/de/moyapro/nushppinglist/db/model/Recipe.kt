package de.moyapro.nushppinglist.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Recipe(
    @PrimaryKey(autoGenerate = true)
    var recipeId: Long,
    val title: String,
) {
}
