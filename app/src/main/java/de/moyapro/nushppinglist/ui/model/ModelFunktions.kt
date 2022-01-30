package de.moyapro.nushppinglist.ui.model

import android.util.Log
import de.moyapro.nushppinglist.constants.SWITCHES
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

const val tag = "StateListener"

fun <T> MutableStateFlow<T>.listenTo(source: Flow<T>, coroutineScope: CoroutineScope) {
    return this.listenTo(source, coroutineScope) { x ->
        if (SWITCHES.DEBUG) {
            Log.i(tag, "value: $x")

        }
        x
    }
}

fun <T, R> MutableStateFlow<R>.listenTo(
    source: Flow<T>,
    coroutineScope: CoroutineScope,
    transformation: ((T) -> R),
) {
    val that = this
    coroutineScope.launch {
        source.collect { valuesFromSource ->
            if (SWITCHES.DEBUG) {
                Log.i(tag, "value: $valuesFromSource")
            }
            that.value = transformation(valuesFromSource)
        }
    }
}
