package com.example.jairosofttimesheet.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jairosofttimesheet.data.model.Attendance
import com.example.jairosofttimesheet.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

open class AttendanceViewModel(
    private val repository: Repository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

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

    // Fetch from API
    fun fetchAttendanceFromApi() {
        viewModelScope.launch {
            try {
                val logs = repository.getAttendance()
                println("✅ Attendance logs fetched: ${logs.size}")
                logs.forEach { println(it) }

                _attendanceList.value = logs
            } catch (e: Exception) {
                println("❌ Error fetching attendance: ${e.message}")
                e.printStackTrace()
            }
        }
    }


    fun toggleClockIn() {
        _isClockedIn.value = !_isClockedIn.value
    }

    private fun saveAttendanceData(data: List<Attendance>) {
        savedStateHandle["attendanceData"] = data
    }

    private fun getSavedAttendanceData(): List<Attendance> {
        return savedStateHandle["attendanceData"] ?: emptyList()
    }
}
