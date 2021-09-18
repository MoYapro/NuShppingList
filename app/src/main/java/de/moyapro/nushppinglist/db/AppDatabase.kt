package de.moyapro.nushppinglist.db

import androidx.room.Database
import androidx.room.RoomDatabase
import de.moyapro.nushppinglist.db.model.CartDao
import de.moyapro.nushppinglist.db.model.CartItemProperties
import de.moyapro.nushppinglist.db.model.Item

@Database(entities = [Item::class, CartItemProperties::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}
