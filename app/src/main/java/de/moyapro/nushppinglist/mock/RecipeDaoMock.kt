package de.moyapro.nushppinglist.mock

import de.moyapro.nushppinglist.db.dao.RecipeDao
import de.moyapro.nushppinglist.db.model.Recipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class RecipeDaoMock(
    private val externalScope: CoroutineScope
): RecipeDao {
    override fun save(vararg recipe: Recipe) {
        TODO("Not yet implemented")
    }

    override fun findAllRecipe(): Flow<List<Recipe>> {
        TODO("Not yet implemented")
    }
}
