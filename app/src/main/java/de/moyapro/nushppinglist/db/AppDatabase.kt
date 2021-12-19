package de.moyapro.nushppinglist.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.dao.RecipeDao
import de.moyapro.nushppinglist.db.model.*
import de.moyapro.nushppinglist.ui.model.converter.Converters
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Item::class, CartItemProperties::class, RecipeItem::class, RecipeProperties::class, RecipeStep::class],
    version = 5)
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
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
