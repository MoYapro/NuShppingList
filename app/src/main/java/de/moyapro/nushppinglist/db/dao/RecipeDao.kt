package de.moyapro.nushppinglist.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import de.moyapro.nushppinglist.db.model.Recipe
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Transaction
    @Insert
    fun save(vararg recipe: Recipe)

    @Transaction
    @Query("select * from Recipe")
    fun findAllRecipe(): Flow<List<Recipe>>

}
