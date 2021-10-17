package de.moyapro.nushppinglist.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import de.moyapro.nushppinglist.db.model.Recipe
import de.moyapro.nushppinglist.db.model.RecipeItem
import de.moyapro.nushppinglist.db.model.RecipeProperties
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Transaction
    @Insert
    fun save(vararg recipe: RecipeItem)

    @Transaction
    @Insert
    fun save(recipe: RecipeProperties)

    @Transaction
    @Query("select * from RecipeProperties")
    fun findAllRecipe(): Flow<List<Recipe>>


}
