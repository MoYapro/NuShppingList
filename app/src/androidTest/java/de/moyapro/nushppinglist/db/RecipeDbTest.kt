package de.moyapro.nushppinglist.db

import androidx.test.ext.junit.runners.AndroidJUnit4
import de.moyapro.nushppinglist.db.dao.RecipeDao
import de.moyapro.nushppinglist.db.model.Recipe
import de.moyapro.nushppinglist.db.model.RecipeProperties
import de.moyapro.nushppinglist.ui.model.RecipeViewModel
import de.moyapro.nushppinglist.util.DbTestHelper
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RecipeDbTest {
    private lateinit var recipeDao: RecipeDao
    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        db = DbTestHelper.createAppDatabase()
        recipeDao = db.recipeDao()
        recipeViewModel = RecipeViewModel(recipeDao)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test(timeout = 10000)
    @Throws(Exception::class)
    fun writeAndLoadRecipe() = runBlocking {
        val recipe = Recipe(RecipeProperties(1, 1, "Cake"), 1)
        recipeViewModel.save(recipe)
        val dbRecipe: Recipe = recipeDao.findAllRecipe().first().first()
        recipe.recipeId shouldBe dbRecipe.recipeId
        recipe.recipeProperties.title shouldBe dbRecipe.recipeProperties.title
    }

    // turbine example
//    @OptIn(ExperimentalTime::class)
//    @Test(timeout = 10000)
//    fun addExistingItemByName() = runBlockingTest {
//        val itemName = "Milk"
//        val newItem = Item(itemName)
//        viewModel.add(newItem)
//        viewModel.addToCart(itemName)
//
//        viewModel.allCartItems.test { <-------------------- here
//            val cartItem = awaitItem()
//            cartItem.single().item.name shouldBe itemName
//        }
//    }

}
