package com.example.jairosofttimesheet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jairosofttimesheet.data.model.Attendance
import com.example.jairosofttimesheet.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class AttendanceViewModel(private val repository: Repository) : ViewModel() {

    private val _attendanceList = MutableStateFlow<List<Attendance>>(emptyList())
    open val attendanceList: StateFlow<List<Attendance>> = _attendanceList

    private val _isClockedIn = MutableStateFlow(false)
    open val isClockedIn: StateFlow<Boolean> = _isClockedIn

    init {
        fetchAttendance()
    }

    private fun fetchAttendance() {
        viewModelScope.launch {
            try {
                val data = repository.getAttendance()
                _attendanceList.value = data
            } catch (e: Exception) {
                e.printStackTrace() // You can hook this to error state
            }
        }
    }

    fun fetchAttendanceFromApi() {
        viewModelScope.launch {
            val logs = repository.getAttendance() // Or getAttendance()
            _attendanceList.value = logs

        }
    }


    fun addAttendance(location: String, date: String, timeIn: String) {
        val newAttendance = Attendance(location, date, timeIn, "--")
        val updatedList = _attendanceList.value + newAttendance
        _attendanceList.value = updatedList
        _isClockedIn.value = true
    }

    fun updateTimeOut() {
        val updatedList = _attendanceList.value.toMutableList()
        if (updatedList.isNotEmpty() && updatedList.last().timeOut == "--") {
            updatedList[updatedList.lastIndex] = updatedList.last().copy(timeOut = "05:00 PM")
            _attendanceList.value = updatedList
            _isClockedIn.value = false
        }
    }

    fun toggleClockIn() {
        _isClockedIn.value = !_isClockedIn.value
    }
}
