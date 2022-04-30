package de.moyapro.nushppinglist.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime


class SubscriptionViewModel : ViewModel() {
    private var _currentTime = MutableStateFlow<List<LocalDateTime>>(emptyList())
    var currentTime = _currentTime
    var currentTimeEmitter: Job? = null

    @OptIn(ExperimentalTime::class)
    fun startTimer() {
        currentTimeEmitter = viewModelScope.launch {
            repeat(Int.MAX_VALUE) {
                _currentTime.value += LocalDateTime.now()
                delay(1.seconds)
            }
        }
    }

    fun stopTimer() {
        currentTimeEmitter?.cancel("end timer")
    }

}
