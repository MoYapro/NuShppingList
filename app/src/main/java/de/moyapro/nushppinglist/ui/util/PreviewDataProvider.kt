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
                createSampleRecipeCake(recipeId = 1, title = "Cake"),
                createSampleRecipeCake(recipeId = 1, title = "I"),
                createSampleRecipeCake(recipeId = 1, title = "Wonder"),
                createSampleRecipeCake(recipeId = 1,
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


fun createSampleItem() = Item(
    itemId = ItemId(1),
    name = "Sugar",
    defaultItemAmount = 1000,
    defaultItemUnit = "g")

fun createSampleCartItem(recipeId: RecipeId = RecipeId(-1)) =
    CartItem(createSampleItem()).apply { cartItemProperties.recipeId = recipeId }


fun createSampleRecipeItem(recipeId: Long = 1) = RecipeItem(
    recipeItemId = Random.nextLong(),
    recipeId = RecipeId(recipeId),
    amount = 14.0,
    item = Item(itemId = ItemId(99),
        name = "Bacon",
        defaultItemAmount = 250,
        defaultItemUnit = "g"),
)

fun createSampleRecipeCake(recipeId: Long = 1, title: String = "Cake"): Recipe {
    return Recipe(
        RecipeProperties(
            recipePropertiesId = Random.nextLong(),
            recipeId = RecipeId(recipeId),
            title = title,
            description = "This is some tasty cake",
        ),
        recipeId = RecipeId(recipeId),
        recipeItems = listOf(
            RecipeItem(
                recipeItemId = Random.nextLong(),
                recipeId = RecipeId(recipeId),
                amount = 0.3,
                item = Item(itemId = ItemId(30),
                    name = "Milk",
                    defaultItemAmount = 1,
                    defaultItemUnit = "l"),
            ),
            RecipeItem(
                recipeItemId = Random.nextLong(),
                recipeId = RecipeId(recipeId),
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
                recipeId = RecipeId(recipeId),
                stepNumber = 1,
                stepDescription = "Now add the eggs"
            ),
            RecipeStep(
                recipeStepId = Random.nextLong(),
                recipeId = RecipeId(recipeId),
                stepNumber = 2,
                stepDescription = "In the end finish with a bang"
            ),
        )
    )
}

fun createSampleRecipeNoodels(recipeId: Long = 2, title: String = "Noodles"): Recipe {
    return Recipe(
        RecipeProperties(
            recipePropertiesId = Random.nextLong(),
            recipeId = RecipeId(recipeId),
            title = title,
            description = "This is some tasty cake",
        ),
        recipeId = RecipeId(recipeId),
        recipeItems = listOf(
            RecipeItem(
                recipeItemId = Random.nextLong(),
                recipeId = RecipeId(recipeId),
                amount = 0.3,
                item = Item(itemId = ItemId(60),
                    name = "Noodels",
                    defaultItemAmount = 1,
                    defaultItemUnit = "l"),
            ),
            RecipeItem(
                recipeItemId = Random.nextLong(),
                recipeId = RecipeId(recipeId),
                amount = 12.0,
                item = Item(itemId = ItemId(61),
                    name = "Tomato",
                    defaultItemAmount = 1000,
                    defaultItemUnit = "g"),
            ),
        )
    )
}


