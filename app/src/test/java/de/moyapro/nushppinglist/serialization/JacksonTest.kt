package de.moyapro.nushppinglist.serialization

import com.fasterxml.jackson.module.kotlin.readValue
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.sync.RequestItemMessage
import org.junit.Test

class JacksonTest {

    @Test
    fun requestItemMessage() {
        val message = RequestItemMessage(ItemId())
        val messageAsString = ConfiguredObjectMapper().writeValueAsString(message)
        val result: RequestItemMessage = ConfiguredObjectMapper().readValue(messageAsString)
    }

}
