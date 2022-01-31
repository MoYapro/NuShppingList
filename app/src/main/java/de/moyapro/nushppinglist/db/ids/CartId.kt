package de.moyapro.nushppinglist.db.ids

import com.fasterxml.jackson.annotation.JsonValue
import java.util.*

@JvmInline
value class CartId (@JsonValue val id: UUID = UUID.randomUUID())
