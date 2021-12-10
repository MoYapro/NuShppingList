package de.moyapro.nushppinglist.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.dao.RecipeDao
import de.moyapro.nushppinglist.db.model.*
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Item::class, CartItemProperties::class, RecipeItem::class, RecipeProperties::class, RecipeStep::class],
    version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun recipeDao(): RecipeDao
    private class AppDatabaseCallback(private val scope: CoroutineScope) :
        RoomDatabase.Callback() {

    }

    companion object {
        /**
         * Singleton prevents multiple instances of database opening at the same time
         */
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nuShoppingListDatabase"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
