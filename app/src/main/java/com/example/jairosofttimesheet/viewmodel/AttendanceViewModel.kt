package com.example.jairosofttimesheet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jairosofttimesheet.data.remote.ApiService
import com.example.jairosofttimesheet.data.remote.LogEntry
import com.example.jairosofttimesheet.data.remote.retrofit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AttendanceViewModel : ViewModel() {

    private val apiService = retrofit.create(ApiService::class.java)

    private val _attendanceLogs = MutableStateFlow<List<LogEntry>>(emptyList())
    val attendanceLogs: StateFlow<List<LogEntry>> = _attendanceLogs

    private val _isClockedIn = MutableStateFlow(false)
    val isClockedIn: StateFlow<Boolean> = _isClockedIn

    fun fetchAttendanceFromApi() {
        viewModelScope.launch {
            try {
                val response = apiService.getAttendanceLogs()
                _attendanceLogs.value = response.response.logs
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addAttendance(location: String, date: String, timeIn: String) {
        // This is a placeholder for future implementation
    }

    fun updateTimeOut() {
        _isClockedIn.value = false
    }

    fun clockIn() {
        _isClockedIn.value = true
    }

    fun clockOut() {
        _isClockedIn.value = false
    }

    fun formatTimestamp(timestamp: Long): String {
        return try {
            val date = Date(timestamp)
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date)
        } catch (e: Exception) {
            "--"
        }
    }
}
