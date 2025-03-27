package com.example.jairosofttimesheet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

open class ProfileViewModel : ViewModel() {
    private val _trackedHours = MutableStateFlow<Map<String, Float>>(emptyMap())
    open val trackedHours: StateFlow<Map<String, Float>> = _trackedHours

    private val _isClockedIn = MutableStateFlow(false)
    val isClockedIn: StateFlow<Boolean> = _isClockedIn

    private val _runningTime = MutableStateFlow(0L)
    open val runningTime: StateFlow<Long> = _runningTime

    private val _attendanceList = MutableStateFlow<List<Triple<String, String, String>>>(emptyList())
    val attendanceList: StateFlow<List<Triple<String, String, String>>> = _attendanceList

    private var clockInTimes = mutableMapOf<String, Long>()
    private var job: Job? = null

    fun clockIn(day: String) {
        if (!_isClockedIn.value) {
            _isClockedIn.value = true
            clockInTimes[day] = System.currentTimeMillis()
            startTimer(day)

            // Store clock-in data
            val date = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date())
            val timeIn = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())
            _attendanceList.value += Triple(date, timeIn, "--")
        }
    }

    fun clockOut(day: String) {
        clockInTimes[day]?.let { startTime ->
            val elapsedMillis = System.currentTimeMillis() - startTime
            val elapsedHours = elapsedMillis / (1000 * 60 * 60).toFloat()

            _trackedHours.value = _trackedHours.value.toMutableMap().apply {
                this[day] = (this[day] ?: 0f) + elapsedHours
            }
        }

        _isClockedIn.value = false
        clockInTimes.remove(day)
        stopTimer()

        // Update last record with time-out
        val timeOut = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())
        _attendanceList.value = _attendanceList.value.mapIndexed { index, record ->
            if (index == _attendanceList.value.lastIndex && record.third == "--") {
                record.copy(third = timeOut)
            } else {
                record
            }
        }
    }

    private fun startTimer(day: String) {
        job?.cancel()
        job = viewModelScope.launch {
            while (_isClockedIn.value) {
                val elapsedMillis = System.currentTimeMillis() - (clockInTimes[day] ?: 0L)
                _runningTime.value = elapsedMillis / 1000

                val elapsedHours = elapsedMillis / (1000 * 60 * 60).toFloat()
                _trackedHours.value = _trackedHours.value.toMutableMap().apply {
                    this[day] = elapsedHours
                }

                delay(1000)
            }
        }
    }

    private fun stopTimer() {
        job?.cancel()
        _runningTime.value = 0
    }
}
