package de.moyapro.nushppinglist.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.moyapro.nushppinglist.db.dao.RecipeDao
import de.moyapro.nushppinglist.db.model.Recipe
import de.moyapro.nushppinglist.mock.RecipeDaoMock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow

@FlowPreview
class RecipeViewModel(
    private val recipeDao: RecipeDao,
) : ViewModel() {

    private val _allRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val allRecipes = _allRecipes

    init {
        _allRecipes.listenTo(recipeDao.findAllRecipe(), viewModelScope)

    }

    fun save(vararg recipes: Recipe) {
        recipes.forEach { recipe ->
            recipeDao.save(recipe.recipeProperties)
            recipeDao.save(*recipe.recipeItems.toTypedArray())
            recipeDao.save(*recipe.recipeSteps.toTypedArray())
        }

    }

    @Suppress("unused") // no-args constructor required by 'by viewmodels()'
    constructor() : this(RecipeDaoMock(CoroutineScope(Dispatchers.IO + SupervisorJob())))
}

