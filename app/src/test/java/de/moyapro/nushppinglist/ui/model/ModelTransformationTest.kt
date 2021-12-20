package de.moyapro.nushppinglist.ui.model

import de.moyapro.nushppinglist.db.model.RecipeId
import de.moyapro.nushppinglist.ui.util.createSampleCartItem
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.Test

class ModelTransformationTest {
    @Test
    fun groupCartItemsByRecipe__noRecipe() {
        val cartItem = createSampleCartItem(recipeId = null)
        val result = ModelTransformation.groupCartItemsByRecipe(listOf(cartItem))
        result[null]?.single() shouldBe cartItem
    }

    @Test
    fun groupCartItemsByRecipe__noRecipeAndRecipes() {
        val recipeId = RecipeId()
        val cartItems = listOf(
            createSampleCartItem(recipeId = null),
            createSampleCartItem(recipeId),
            createSampleCartItem(recipeId)
        )
        val result = ModelTransformation.groupCartItemsByRecipe(cartItems)
        result[null]!! shouldHaveSize 1
        result[recipeId]!! shouldHaveSize 2
    }
}
