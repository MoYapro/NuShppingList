@file:Suppress("ClassName")

package de.moyapro.nushppinglist.db.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


object Migration_8_9 : Migration(8, 9) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("alter table 'Cart' add column selected INTEGER NOT NULL DEFAULT 0")
        database.execSQL("alter table 'CartItemProperties' add column inCart BLOB NOT NULL DEFAULT 0")
    }
}
