package de.moyapro.nushppinglist.util

fun waitFor(predicate: () -> Boolean) {
    while (!predicate()) {
        Thread.sleep(100)
    }
}
