package de.moyapro.nushppinglist.ui.util

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import de.moyapro.nushppinglist.constants.UNIT.GRAMM
import de.moyapro.nushppinglist.constants.UNIT.LITER
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.db.model.*
import java.math.BigDecimal
import java.util.*

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
                createSampleRecipeCake(title = "Cake"),
                createSampleRecipeCake(title = "I"),
                createSampleRecipeCake(title = "Wonder"),
                createSampleRecipeCake(
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


fun createSampleItem(name: String = "Sugar") = Item(
    itemId = ItemId(UUID.fromString("c871a987-54ac-4f88-8e57-054a3507db5a")),
    name = name,
    description = "weiss",
    defaultItemAmount = 1000,
    defaultItemUnit = GRAMM,
    price = BigDecimal.ZERO,
)

fun createSampleCartItem(recipeId: RecipeId? = RecipeId()) =
    CartItem(createSampleItem()).apply {
        cartItemProperties.cartItemPropertiesId =
            UUID.fromString("5cf9d5fd-f181-4620-86ec-7a597219cb12")
        cartItemProperties.recipeId = recipeId
    }


fun createSampleRecipeItem(recipeId: UUID = UUID.randomUUID()) = RecipeItem(
    recipeId = RecipeId(recipeId),
    amount = 14.0,
    item = Item(
        itemId = ItemId(),
        name = "Bacon",
        description = "Pork",
        defaultItemAmount = 250,
        defaultItemUnit = GRAMM,
        price = BigDecimal.ZERO,
    ),
)

fun createSampleRecipeCake(recipeId: UUID = UUID.randomUUID(), title: String = "Cake"): Recipe {
    return Recipe(
        RecipeProperties(
            recipeId = RecipeId(recipeId),
            title = title,
            description = "This is some tasty cake",
        ),
        recipeId = RecipeId(recipeId),
        recipeItems = listOf(
            RecipeItem(
                recipeId = RecipeId(recipeId),
                amount = 0.3,
                item = Item(
                    itemId = ItemId(),
                    name = "Milk",
                    description = "organic",
                    defaultItemAmount = 1,
                    defaultItemUnit = LITER,
                    price = BigDecimal.ZERO,
                ),
            ),
            RecipeItem(
                recipeId = RecipeId(recipeId),
                amount = 12.0,
                item = Item(
                    itemId = ItemId(),
                    name = "Sugar",
                    description = "white",
                    defaultItemAmount = 1000,
                    defaultItemUnit = GRAMM,
                    price = BigDecimal.ZERO,
                ),
            ),
        ),
        recipeSteps = listOf(
            RecipeStep(
                recipeId = RecipeId(recipeId),
                stepNumber = 1,
                stepDescription = "Now add the eggs"
            ),
            RecipeStep(
                recipeId = RecipeId(recipeId),
                stepNumber = 2,
                stepDescription = "In the end finish with a bang"
            ),
        )
    )
}

fun createSampleRecipeNoodels(
    recipeId: RecipeId = RecipeId(),
    title: String = "Noodles",
): Recipe {
    return Recipe(
        RecipeProperties(
            recipeId = recipeId,
            title = title,
            description = "This is some tasty cake",
        ),
        recipeId = recipeId,
        recipeItems = listOf(
            RecipeItem(
                recipeId = recipeId,
                amount = 0.3,
                item = Item(
                    itemId = ItemId(),
                    name = "Noodels",
                    description = "instant",
                    defaultItemAmount = 1,
                    defaultItemUnit = LITER,
                    price = BigDecimal.ZERO,
                ),
            ),
            RecipeItem(
                recipeId = recipeId,
                amount = 12.0,
                item = Item(
                    itemId = ItemId(),
                    name = "Tomato",
                    description = "sweet, italy",
                    defaultItemAmount = 1000,
                    defaultItemUnit = GRAMM,
                    price = BigDecimal.ZERO,
                ),
            ),
        )
    )
}


