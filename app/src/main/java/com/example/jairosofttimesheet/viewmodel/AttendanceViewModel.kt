package com.example.jairosofttimesheet.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import com.example.jairosofttimesheet.data.model.Attendance
import java.util.*

open class AttendanceViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _attendanceList = MutableStateFlow<List<Attendance>>(getSavedAttendanceData())
    val attendanceList: StateFlow<List<Attendance>> = _attendanceList

    private val _isClockedIn = MutableStateFlow(false)
    open val isClockedIn: StateFlow<Boolean> = _isClockedIn

    // Add attendance and save
    fun addAttendance(location: String, date: String, timeIn: String) {
        val newAttendance = Attendance(location, date, timeIn, "--")
        val updatedList = _attendanceList.value + newAttendance
        _attendanceList.value = updatedList
        saveAttendanceData(updatedList)
        _isClockedIn.value = true
    }

    // Update time-out and save
    fun updateTimeOut() {
        val updatedList = _attendanceList.value.toMutableList()
        if (updatedList.isNotEmpty() && updatedList.last().timeOut == "--") {
            val timeOut = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())
            updatedList[updatedList.lastIndex] = updatedList.last().copy(timeOut = timeOut)
            _attendanceList.value = updatedList
            saveAttendanceData(updatedList)
            _isClockedIn.value = false
        }
    }

    // Toggle clock in/out
    fun toggleClockIn() {
        _isClockedIn.value = !_isClockedIn.value
    }

    // Save attendance data
    private fun saveAttendanceData(data: List<Attendance>) {
        savedStateHandle["attendanceData"] = data
    }

    // Retrieve saved attendance data
    private fun getSavedAttendanceData(): List<Attendance> {
        return savedStateHandle["attendanceData"] ?: emptyList()
    }
}


