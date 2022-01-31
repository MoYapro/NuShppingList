package de.moyapro.nushppinglist.db.ids

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.util.*

@JvmInline
value class CartId (@JsonValue val id: UUID = UUID.randomUUID()) {
    companion object {
        @JsonCreator
        @JvmStatic
        fun create(id: String) = CartId(UUID.fromString(id))
    }

    constructor(id: String) :this(UUID.fromString(id))

    constructor(): this(UUID.randomUUID())

}
