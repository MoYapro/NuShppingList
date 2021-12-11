package de.moyapro.nushppinglist.ui.model

import androidx.compose.ui.graphics.Color
import de.moyapro.nushppinglist.ui.util.ToggleList
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.Test

class ToggleListTest {

    private val testOnColor = Color.Cyan
    private val testOffColor = Color.Magenta

    @Test
    fun getValue() {
        val value = 7
        ToggleList(
            containedValues = listOf(value),
            onValue = testOnColor,
            offValue = testOffColor
        )
            .getValue(value) shouldBe testOnColor
    }

    @Test
    fun containsNot() {
        val value = 7
        val otherValue = 8
        ToggleList(
            containedValues = listOf(value),
            onValue = testOnColor,
            offValue = testOffColor
        )
            .getValue(otherValue) shouldBe testOffColor
    }

    @Test
    fun toggleValue() {
        val value = 7
        val toggleList = ToggleList(
            containedValues = listOf<Int>(),
            onValue = testOnColor,
            offValue = testOffColor
        )
        toggleList.getValue(value) shouldBe testOffColor
        toggleList.toggle(value).getValue(value) shouldBe testOnColor
        toggleList.toggle(value).toggle(value).getValue(value) shouldBe testOffColor
    }

    @Test
    fun toggleActive() {
        ToggleList(
            containedValues = listOf<Int>(),
            onValue = testOnColor,
            offValue = testOffColor,
            isActive = false
        )
            .toggleActive().isActive shouldBe true
        ToggleList(
            containedValues = listOf<Int>(),
            onValue = testOnColor,
            offValue = testOffColor,
            isActive = true
        )
            .toggleActive().isActive shouldBe false
    }

    @Test
    fun inactiveToggleList__getValue() {
        val value = 1
        val toggleList = ToggleList(
            containedValues = listOf<Int>(),
            onValue = testOnColor,
            offValue = testOffColor,
            isActive = false
        )
        toggleList.getValue(value) shouldBe null
        toggleList.toggleActive()
        toggleList.getValue(value) shouldBe null
    }

    @Test
    fun inactiveToggleList__toggle() {
        val value = 1
        val toggleList = ToggleList(
            containedValues = listOf<Int>(),
            onValue = testOnColor,
            offValue = testOffColor,
            isActive = false
        )
        toggleList.toggle(value)
        toggleList.toggleActive()
        toggleList.getValue(value) shouldBe null
    }

    @Test
    fun toggle__createsNewInstance() {
        val toggleList = ToggleList(
            containedValues = listOf<Int>(),
            onValue = testOnColor,
            offValue = testOffColor,
        )
        val newToggleList = toggleList.toggle(1)
        newToggleList shouldNotBe toggleList
    }

    @Test
    fun toggleActive__createsNewInstance() {
        val toggleList = ToggleList(
            containedValues = listOf<Int>(),
            onValue = testOnColor,
            offValue = testOffColor,
        )
        val newToggleList = toggleList.toggleActive()
        newToggleList shouldNotBe toggleList
    }

    @Test
    fun getIncludedValues() {
        val toggleList = ToggleList(
            containedValues = listOf(1, 2, 5),
            onValue = testOnColor,
            offValue = testOffColor,
        )
        toggleList.containedValues shouldContainExactlyInAnyOrder listOf(1, 2, 5)
    }

    @Test
    fun contains() {
        val toggleList = ToggleList(
            containedValues = listOf(1, 2, 5),
            onValue = testOnColor,
            offValue = testOffColor,
        )
        toggleList.contains(1) shouldBe  true
        toggleList.contains(2) shouldBe true
        toggleList.contains(3) shouldBe false
    }
}
