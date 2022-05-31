@file:Suppress("ClassName")

package de.moyapro.nushppinglist.db.migration

import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


class Migration_8_9 : AutoMigrationSpec, Migration(8, 9) {

    override fun onPostMigrate(db: SupportSQLiteDatabase) {
        executeMigration(db)
    }

    override fun migrate(database: SupportSQLiteDatabase) {
        executeMigration(database)
    }

    private fun executeMigration(db: SupportSQLiteDatabase) {
        db.execSQL("alter table 'Cart' add column selected INTEGER NOT NULL DEFAULT 0")
        db.execSQL("alter table 'CartItemProperties' add column inCart BLOB NOT NULL DEFAULT 0")
    }
}
