package com.example.jairosofttimesheet.data.repository

import com.example.jairosofttimesheet.data.model.Attendance
import com.example.jairosofttimesheet.data.model.LoginRequest
import com.example.jairosofttimesheet.data.model.LoginResponse
import com.example.jairosofttimesheet.data.model.LoginUser
import com.example.jairosofttimesheet.data.remote.ApiService
import com.example.jairosofttimesheet.utils.toFormattedTime
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Repository(private val apiService: ApiService) {

    fun loginUser(
        loginRequest: LoginRequest,
        onResult: (success: Boolean, message: String?) -> Unit
    ) {
        val call = apiService.loginUser(loginRequest)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    onResult(true, "Login successful")
                } else {
                    onResult(false, "Invalid email or password")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                onResult(false, "Network error: ${t.localizedMessage}")
            }
        })
    }

    suspend fun getAttendance(): List<Attendance> {
        val response = apiService.getAttendanceLogs()
        println("📥 Raw response: ${response.response}")

        return response.response.map {
            Attendance(
                location = "Davao City",
                date = it.date ?: "--",
                timeIn = it.timeIn?.toLongOrNull()?.toFormattedTime() ?: "--",
                timeOut = it.timeOut?.toLongOrNull()?.toFormattedTime() ?: "--"
            )
        }
    }


    fun getAllUsers(
        token: String,
        onResult: (users: List<LoginUser>?, error: String?) -> Unit
    ) {
        val call = apiService.getAllUsers("Bearer $token")
        call.enqueue(object : Callback<List<LoginUser>> {
            override fun onResponse(call: Call<List<LoginUser>>, response: Response<List<LoginUser>>) {
                if (response.isSuccessful) {
                    onResult(response.body(), null)
                } else {
                    onResult(null, "Failed to fetch users")
                }
            }

            override fun onFailure(call: Call<List<LoginUser>>, t: Throwable) {
                onResult(null, "Network error: ${t.localizedMessage}")
            }
        })
    }
}

// ✅ Move this outside the class
fun Long.toFormattedTime(): String {
    val date = Date(this)
    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return formatter.format(date)
}
