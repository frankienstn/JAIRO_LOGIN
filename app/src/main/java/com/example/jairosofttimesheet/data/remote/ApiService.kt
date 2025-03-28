package com.example.jairosofttimesheet.data.remote

import com.squareup.moshi.Moshi
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.POST
import com.example.jairosofttimesheet.data.model.LoginUser
import com.example.jairosofttimesheet.data.model.LoginRequest
import com.example.jairosofttimesheet.data.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)

private const val BASE_URL = "https://timesheet-63231.bubbleapps.io/api/1.1/wf/"

// Moshi Adapter
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

// Retrofit Instance
val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

data class LogsResponse(
    val status: String,
    val response: LogList
)

data class LogList(
    @Json(name = "Logs") val logs: List<LogEntry>
)

@JsonClass(generateAdapter = true)
data class LogEntry(
    @Json(name = "User_id") val userId: String,
    @Json(name = "Date") val date: String,
    @Json(name = "time-in") val timeIn: Long,
    @Json(name = "time-out") val timeOut: Long
)

// API Interface
interface ApiService {
    @GET("logs")
    suspend fun getAttendanceLogs(): LogsResponse

    @POST("login")
    fun loginUser(
        @Body credentials: LoginRequest
    ): Call<LoginResponse>

    @GET("logs")
    fun getAllUsers(
        @Header("Authorization") token: String
    ): Call<List<LoginUser>>
}


// Singleton API Access
object LoginRequest {

}