package de.moyapro.nushppinglist.ui.util

data class ToggleList<T, V : Any>(
    private val onValue: V,
    private val offValue: V,
    val containedValues: List<T> = listOf(),
    val isActive: Boolean = true,
) {

    fun getValue(reference: T): V? {
        if (!isActive) {
            return null
        }
        return if (containedValues.contains(reference)) {
            onValue
        } else {
            offValue
        }
    }

    fun toggle(value: T): ToggleList<T, V> {
        if (!isActive) {
            return this.copy()
        }
        return if (containedValues.contains(value)) {
            this.copy(containedValues = containedValues.filter { it != value })
        } else {
            this.copy(containedValues = (containedValues + value))
        }
    }

    fun toggleActive(): ToggleList<T, V> {
        return this.copy(isActive = !isActive)
    }

    fun contains(value: T) = containedValues.contains(value)

}
