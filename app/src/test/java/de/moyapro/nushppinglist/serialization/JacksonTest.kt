package de.moyapro.nushppinglist.serialization

import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.sync.messages.*
import de.moyapro.nushppinglist.ui.util.createSampleCartItem
import de.moyapro.nushppinglist.ui.util.createSampleItem
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.*

@RunWith(Parameterized::class)
class JacksonTest(
    private val objectToSerialize: Any,
) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                arrayOf(UUID.randomUUID()),
                arrayOf(RequestItemMessage(ItemId())),
                arrayOf(RequestCartMessage("This is a cart request")),
                arrayOf(ItemMessage(createSampleItem())),
                arrayOf(
                    CartMessage(listOf(
                        createSampleCartItem().cartItemProperties,
                        createSampleCartItem().cartItemProperties
                    ))
                ),
                arrayOf(CartItemUpdateMessage(createSampleCartItem().cartItemProperties))
            )
        }
    }

    @Test
    fun serialize_deserialize() {
        val messageAsString = ConfiguredObjectMapper().writeValueAsString(objectToSerialize)
        val result =
            ConfiguredObjectMapper().readValue(messageAsString, objectToSerialize.javaClass)
        result shouldBeEqualToComparingFields objectToSerialize
    }
}
