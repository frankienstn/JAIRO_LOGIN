package com.example.jairosofttimesheet.data.model

import com.squareup.moshi.Json

data class LoginRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

data class LoginResponse(
    @Json(name = "token") val token: String,
    @Json(name = "user_id") val userId: Int
)

data class LoginUser(
    @Json(name = "email") val email: String,
    @Json(name = "name") val name: String
)

data class Attendance(
    val location: String,
    val date: String,
    val timeIn: String,
    val timeOut: String
)
