package de.moyapro.nushppinglist.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class RecipeProperties(
    @PrimaryKey(autoGenerate = true)
    var recipePropertiesId: Long,
    var recipeId: Long,
    var title: String,
) {

}
