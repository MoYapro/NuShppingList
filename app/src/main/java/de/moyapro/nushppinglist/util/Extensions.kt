package de.moyapro.nushppinglist.util

fun <T, X : Any?> takeIfNotDefault(
    original: T,
    default: T,
    newValues: T,
    fieldAccessor: (T) -> X,
): X {
    return if (fieldAccessor(newValues) != fieldAccessor(default)) {
        fieldAccessor(newValues)
    } else {
        fieldAccessor(original)
    }
}

fun <T, K> MutableMap<K, MutableList<T>>.addOrAppend(key: K, newElement: T) {
    val existing = this[key]
    if (existing != null) {
        existing.add(newElement)
    } else {
        this[key] = mutableListOf(newElement)
    }
}
