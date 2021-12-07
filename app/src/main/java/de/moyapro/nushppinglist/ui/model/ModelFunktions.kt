package de.moyapro.nushppinglist.ui.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun <T> MutableStateFlow<T>.listenTo(source: Flow<T>, coroutineScope: CoroutineScope) {
    return this.listenTo(source, coroutineScope) { x -> x }
}

fun <T, R> MutableStateFlow<R>.listenTo(
    source: Flow<T>,
    coroutineScope: CoroutineScope,
    transformation: ((T) -> R),
) {
    val that = this
    coroutineScope.launch {
        source.collect { valuesFromSource ->
            that.value = transformation(valuesFromSource)
        }
    }
}
