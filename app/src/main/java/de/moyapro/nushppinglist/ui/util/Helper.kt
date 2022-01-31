package de.moyapro.nushppinglist.ui.util

fun waitFor(timeout: Long = 5000, predicate: () -> Boolean) {
    val startTime = System.currentTimeMillis()
    val endTime = startTime + timeout
    while (true) {
        if (predicate()) return
        if (endTime < System.currentTimeMillis()) throw IllegalStateException("condition was not met after $timeout milliseconds")
        Thread.sleep(100)
    }
}
