package de.moyapro.nushppinglist.util

import androidx.room.Room.inMemoryDatabaseBuilder
import androidx.test.core.app.ApplicationProvider
import de.moyapro.nushppinglist.db.AppDatabase

object DbTestHelper {
    fun createTestDatabase(): AppDatabase =
        inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
}
