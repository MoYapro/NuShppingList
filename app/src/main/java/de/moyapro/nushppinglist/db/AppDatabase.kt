package de.moyapro.nushppinglist.db

import android.content.Context
import androidx.room.*
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.dao.RecipeDao
import de.moyapro.nushppinglist.db.model.*
import de.moyapro.nushppinglist.ui.model.converter.Converters
import kotlinx.coroutines.CoroutineScope

@Database(
    entities = [
        Item::class,
        CartItemProperties::class,
        Cart::class,
        RecipeItem::class,
        RecipeProperties::class,
        RecipeStep::class
    ],
    autoMigrations = [
        AutoMigration(from = 7, to = 8)
    ],
    version = 8)
@TypeConverters(Converters::class)
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
