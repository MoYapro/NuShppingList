package de.moyapro.nushppinglist.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import de.moyapro.nushppinglist.db.model.Recipe
import de.moyapro.nushppinglist.db.model.RecipeItem
import de.moyapro.nushppinglist.db.model.RecipeProperties
import de.moyapro.nushppinglist.db.model.RecipeStep
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Transaction
    @Insert
    suspend fun save(vararg recipe: RecipeItem)

    @Transaction
    @Insert
    suspend fun save(vararg recipe: RecipeProperties)

    @Transaction
    @Insert
    suspend fun save(vararg recipe: RecipeStep)

    @Transaction
    @Query("select * from RecipeProperties")
    fun findAllRecipe(): Flow<List<Recipe>>


}
