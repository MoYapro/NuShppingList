package de.moyapro.nushppinglist.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.moyapro.nushppinglist.db.ids.ID

@Entity
class Test(
    @PrimaryKey(autoGenerate = true)
    @get:JvmName("getId")
    @set:JvmName("setId")
    var id: ID,
) {
    constructor() : this(ID(0))
}
