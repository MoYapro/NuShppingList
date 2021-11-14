package de.moyapro.nushppinglist

import de.moyapro.nushppinglist.ui.model.matched
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(value = Parameterized::class)
class SearchTest(
    private val shouldMatch: Boolean,
    private val searchString: String,
    private val itemName: String
) {
    @Test
    fun findItem() {
        when (shouldMatch) {
            true -> assertTrue(
                "Should match for $itemName and $searchString",
                matched(itemName, searchString)
            )
            false -> assertFalse(
                "Should NOT match for $itemName and $searchString",
                matched(itemName, searchString)
            )
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{1} finds {2} -> {0}")
        fun data(): Iterable<Array<Any>> {
            return arrayListOf(
                arrayOf(true, "Milk", "Milk"),
                arrayOf(true, "milk", "Milk"),
                arrayOf(false, "Milka", "Milk"),
                arrayOf(false, "X", "O"),
            )
        }
    }
}
