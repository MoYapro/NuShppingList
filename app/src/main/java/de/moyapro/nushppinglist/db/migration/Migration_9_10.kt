@file:Suppress("ClassName")

package de.moyapro.nushppinglist.db.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


object Migration_9_10 : Migration(9, 10) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `CartItemPropertiesNew` (`cartItemPropertiesId` BLOB NOT NULL, `cartItemId` BLOB NOT NULL, `inCart` BLOB NOT NULL DEFAULT 0, `itemId` BLOB NOT NULL, `recipeId` BLOB, `amount` INTEGER NOT NULL, `checked` INTEGER NOT NULL, PRIMARY KEY(`cartItemPropertiesId`))")
        database.execSQL("insert into `CartItemPropertiesNew` select * from `CartItemProperties`")
        database.execSQL("drop table if exists `CartItemProperties`")
        database.execSQL("alter table `CartItemPropertiesNew` rename to `CartItemProperties`")

    }
}
