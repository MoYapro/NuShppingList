package de.moyapro.nushppinglist.db

import androidx.test.ext.junit.runners.AndroidJUnit4
import de.moyapro.nushppinglist.db.dao.RecipeDao
import de.moyapro.nushppinglist.db.model.Recipe
import de.moyapro.nushppinglist.ui.model.RecipeViewModel
import de.moyapro.nushppinglist.ui.util.createSampleRecipeCake
import de.moyapro.nushppinglist.ui.util.createSampleRecipeNoodels
import de.moyapro.nushppinglist.util.DbTestHelper
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
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
        db = DbTestHelper.createTestDatabase()
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
    fun writeAndLoadRecipe(): Unit = runBlocking {
        val recipeCake = createSampleRecipeCake()
        val recipeNoodles = createSampleRecipeNoodels()

        recipeViewModel.save(recipeCake, recipeNoodles)

        val dbRecipe: Recipe =
            recipeViewModel.allRecipes.take(1).first().single { it.recipeId == recipeCake.recipeId }

        dbRecipe.recipeId shouldBe recipeCake.recipeId
        dbRecipe.recipeProperties.description shouldBe recipeCake.recipeProperties.description
        dbRecipe.recipeProperties.title shouldBe recipeCake.recipeProperties.title
        dbRecipe.recipeItems shouldHaveSize 2
        dbRecipe.recipeItems.map { it.recipeItemId } shouldContainExactlyInAnyOrder recipeCake.recipeItems.map { it.recipeItemId }
        dbRecipe.recipeItems.map { it.recipeId } shouldContainExactlyInAnyOrder recipeCake.recipeItems.map { it.recipeId }
        dbRecipe.recipeItems.map { it.amount } shouldContainExactlyInAnyOrder recipeCake.recipeItems.map { it.amount }
        dbRecipe.recipeItems.map { it.item.itemId } shouldContainExactlyInAnyOrder recipeCake.recipeItems.map { it.item.itemId }
        dbRecipe.recipeSteps shouldHaveSize 2
        dbRecipe.recipeSteps.map { it.recipeId } shouldContainExactlyInAnyOrder recipeCake.recipeSteps.map { it.recipeId }
        dbRecipe.recipeSteps.map { it.recipeStepId } shouldContainExactlyInAnyOrder recipeCake.recipeSteps.map { it.recipeStepId }
        dbRecipe.recipeSteps.map { it.stepNumber } shouldContainExactlyInAnyOrder recipeCake.recipeSteps.map { it.stepNumber }
        dbRecipe.recipeSteps.map { it.stepDescription } shouldContainExactlyInAnyOrder recipeCake.recipeSteps.map { it.stepDescription }

        dbRecipe.recipeId shouldBe recipeCake.recipeId
        dbRecipe.recipeProperties shouldBe recipeCake.recipeProperties
        dbRecipe.recipeItems shouldContainExactlyInAnyOrder recipeCake.recipeItems
        dbRecipe.recipeSteps shouldContainExactlyInAnyOrder recipeCake.recipeSteps

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
