package de.moyapro.nushppinglist.ui.util

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.db.model.*
import kotlin.random.Random

class LabelProvider : PreviewParameterProvider<String> {
    override val values: Sequence<String>
        get() = listOf("The Label").asSequence()
}


class ItemListProvider : PreviewParameterProvider<List<Item>> {
    override val values: Sequence<List<Item>>
        get() = listOf(listOf(Item("Milk"), Item("Apple"))).asSequence()
}

class RecipeListProvider : PreviewParameterProvider<List<Recipe>> {
    override val values: Sequence<List<Recipe>>
        get() = sequenceOf(
            listOf(
                createSampleRecipe(recipeId = 1, title = "Cake"),
                createSampleRecipe(recipeId = 1, title = "I"),
                createSampleRecipe(recipeId = 1, title = "Wonder"),
                createSampleRecipe(recipeId = 1,
                    title = "Icecreem with fancy name and sooo much cream on it that it does not fit the screen AT ALL"),
            )
        )
}


class RecipeProvider : PreviewParameterProvider<Recipe> {
    override val values: Sequence<Recipe>
        get() = RecipeListProvider().values.flatten()
}

class RecipePropertiesProvider : PreviewParameterProvider<RecipeProperties> {
    override val values: Sequence<RecipeProperties>
        get() = RecipeProvider().values.map { it.recipeProperties }
}


fun createSampleRecipe(recipeId: Long = 1, title: String = "Cake"): Recipe {
    return Recipe(
        RecipeProperties(
            recipePropertiesId = Random.nextLong(),
            recipeId = recipeId,
            title = title,
            description = "This is some tasty cake",
        ),
        recipeId = recipeId,
        recipeItems = listOf(
            RecipeItem(
                recipeItemId = Random.nextLong(),
                recipeId = recipeId,
                amount = 0.3,
                item = Item(itemId = ItemId(30),
                    name = "Milk",
                    defaultItemAmount = 1,
                    defaultItemUnit = "l"),
            ),
            RecipeItem(
                recipeItemId = Random.nextLong(),
                recipeId = recipeId,
                amount = 12.0,
                item = Item(itemId = ItemId(31),
                    name = "Sugar",
                    defaultItemAmount = 1000,
                    defaultItemUnit = "g"),
            ),
        ),
        recipeSteps = listOf(
            RecipeStep(
                recipeStepId = Random.nextLong(),
                recipeId = recipeId,
                stepNumber = 1,
                stepDescription = "Now add the eggs"
            ),
            RecipeStep(
                recipeStepId = Random.nextLong(),
                recipeId = recipeId,
                stepNumber = 2,
                stepDescription = "In the end finish with a bang"
            ),
        )
    )
}


