package de.moyapro.nushppinglist

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Item::class, CartItemProperties::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}