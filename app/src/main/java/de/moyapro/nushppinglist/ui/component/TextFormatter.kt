package de.moyapro.nushppinglist.ui

import de.moyapro.nushppinglist.db.model.RecipeItem

fun amountText(recipeItem: RecipeItem): String = "${recipeItem.amount} ${recipeItem.item.defaultItemUnit}"
