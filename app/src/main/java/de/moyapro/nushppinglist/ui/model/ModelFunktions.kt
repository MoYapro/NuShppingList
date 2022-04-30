package de.moyapro.nushppinglist.ui.model

import android.util.Log
import de.moyapro.nushppinglist.constants.SWITCHES
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val tag = "StateListener"

fun <T> MutableStateFlow<T>.listenTo(source: Flow<T>, coroutineScope: CoroutineScope): Job {
    return this.listenTo(source, coroutineScope) { x ->
        if (SWITCHES.DEBUG) {
            Log.d(tag, "load value: $x")
        }
        x
    }
}

fun <T, R> MutableStateFlow<R>.listenTo(
    source: Flow<T>,
    coroutineScope: CoroutineScope,
    transformation: ((T) -> R),
): Job {
    val that = this
    return coroutineScope.launch {
        source.collect { valuesFromSource ->
            if (SWITCHES.DEBUG) {
                Log.d(tag, "load and transform value: $valuesFromSource")
            }
            that.value = transformation(valuesFromSource)
        }
    }
}
