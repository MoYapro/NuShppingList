package de.moyapro.nushppinglist.mock

import de.moyapro.nushppinglist.db.dao.RecipeDao
import de.moyapro.nushppinglist.db.model.Recipe
import de.moyapro.nushppinglist.db.model.RecipeItem
import de.moyapro.nushppinglist.db.model.RecipeProperties
import de.moyapro.nushppinglist.db.model.RecipeStep
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class RecipeDaoMock(
    private val externalScope: CoroutineScope
): RecipeDao {

    override fun save(vararg recipe: RecipeItem) {
        TODO("Not yet implemented")
    }

    override fun save(vararg recipe: RecipeProperties) {
        TODO("Not yet implemented")
    }

    override fun save(vararg recipe: RecipeStep) {
        TODO("Not yet implemented")
    }

    override fun findAllRecipe(): Flow<List<Recipe>> {
        TODO("Not yet implemented")
    }
}
