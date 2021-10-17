package de.moyapro.nushppinglist.db

import androidx.room.Database
import androidx.room.RoomDatabase
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.dao.RecipeDao
import de.moyapro.nushppinglist.db.model.CartItemProperties
import de.moyapro.nushppinglist.db.model.Item
import de.moyapro.nushppinglist.db.model.Recipe

@Database(entities = [Item::class, CartItemProperties::class, Recipe::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun recipeDao(): RecipeDao
}
