package de.moyapro.nushppinglist.ui.util

fun waitFor( timeout:Long = 2000, predicate: () -> Boolean) {
    val startTime = System.currentTimeMillis()
    val endTime = startTime + timeout
    while (!predicate() && endTime > System.currentTimeMillis()) {
        Thread.sleep(100)
    }
}
