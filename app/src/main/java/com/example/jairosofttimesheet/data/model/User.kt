package com.example.jairosofttimesheet.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "status") val status: String,
    @Json(name = "response") val response: LoginData
)

@JsonClass(generateAdapter = true)
data class LoginData(
    @Json(name = "token") val token: String,
    @Json(name = "user_id") val userId: String,
    @Json(name = "expires") val expires: Long
)

@JsonClass(generateAdapter = true)
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
