package de.moyapro.nushppinglist.serialization

import de.moyapro.nushppinglist.constants.CONSTANTS.DEFAULT_CART
import de.moyapro.nushppinglist.db.ids.CartId
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.sync.messages.CartMessage
import de.moyapro.nushppinglist.sync.messages.ItemMessage
import de.moyapro.nushppinglist.sync.messages.RequestCartMessage
import de.moyapro.nushppinglist.sync.messages.RequestItemMessage
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
            val cartId = CartId()
            return listOf(
                arrayOf(UUID(0, 0)),
                arrayOf(UUID.randomUUID()),
                arrayOf(DEFAULT_CART),
                arrayOf(RequestItemMessage(ItemId())),
                arrayOf(RequestCartMessage(cartId)),
                arrayOf(ItemMessage(createSampleItem())),
                arrayOf(
                    CartMessage(listOf(
                        createSampleCartItem(inCart = cartId).cartItemProperties,
                        createSampleCartItem(inCart = cartId).cartItemProperties
                    ),
                        cartId)
                ),
                arrayOf(CartMessage(listOf(createSampleCartItem(inCart = cartId).cartItemProperties),
                    cartId))
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
