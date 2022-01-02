package de.moyapro.nushppinglist.ui.util

fun waitFor(predicate: () -> Boolean) {
    while (!predicate()) {
        Thread.sleep(100)
    }
}
