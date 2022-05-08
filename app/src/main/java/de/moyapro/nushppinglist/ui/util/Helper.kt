package de.moyapro.nushppinglist.ui.util

fun waitFor(timeout: Long = 5000, predicate: () -> Boolean): Boolean {
    val startTime = System.currentTimeMillis()
    val endTime = startTime + timeout
    while (true) {
        if (predicate()) return true
        if (endTime < System.currentTimeMillis()) return false
        Thread.sleep(100)
    }
    return false
}

suspend fun <T> Iterable<T>.forEach(action: suspend (T) -> Unit) {
    for (element in this) action(element)
}
