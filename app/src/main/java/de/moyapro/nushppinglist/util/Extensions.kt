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
