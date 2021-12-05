package de.moyapro.nushppinglist.db

import androidx.test.ext.junit.runners.AndroidJUnit4
import de.moyapro.nushppinglist.db.dao.RecipeDao
import de.moyapro.nushppinglist.db.model.Recipe
import de.moyapro.nushppinglist.ui.model.RecipeViewModel
import de.moyapro.nushppinglist.ui.util.createSampleRecipeCake
import de.moyapro.nushppinglist.util.DbTestHelper
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
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
        val recipe = createSampleRecipeCake(recipeId = 1)
        val recipe2 = createSampleRecipeCake(recipeId = 2)

        recipeViewModel.save(recipe, recipe2)

        val dbRecipe: Recipe = recipeDao.findAllRecipe().first().first()

        dbRecipe.recipeId shouldBe recipe.recipeId
        dbRecipe.recipeProperties.description shouldBe recipe.recipeProperties.description
        dbRecipe.recipeProperties.title shouldBe recipe.recipeProperties.title
        dbRecipe.recipeItems shouldHaveSize 2
        dbRecipe.recipeItems.map { it.recipeItemId } shouldContainExactlyInAnyOrder recipe.recipeItems.map { it.recipeItemId }
        dbRecipe.recipeItems.map { it.recipeId } shouldContainExactlyInAnyOrder recipe.recipeItems.map { it.recipeId }
        dbRecipe.recipeItems.map { it.amount } shouldContainExactlyInAnyOrder recipe.recipeItems.map { it.amount }
        dbRecipe.recipeItems.map { it.item.itemId } shouldContainExactlyInAnyOrder recipe.recipeItems.map { it.item.itemId }
        dbRecipe.recipeSteps shouldHaveSize 2
        dbRecipe.recipeSteps.map { it.recipeId } shouldContainExactlyInAnyOrder recipe.recipeSteps.map { it.recipeId }
        dbRecipe.recipeSteps.map { it.recipeStepId } shouldContainExactlyInAnyOrder recipe.recipeSteps.map { it.recipeStepId }
        dbRecipe.recipeSteps.map { it.stepNumber } shouldContainExactlyInAnyOrder recipe.recipeSteps.map { it.stepNumber }
        dbRecipe.recipeSteps.map { it.stepDescription } shouldContainExactlyInAnyOrder recipe.recipeSteps.map { it.stepDescription }

        dbRecipe shouldBe recipe

        Unit
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
