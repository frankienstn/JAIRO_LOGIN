package com.example.jairosofttimesheet.data.remote

import com.example.jairosofttimesheet.data.model.LoginRequest
import com.example.jairosofttimesheet.data.model.LoginResponse
import com.example.jairosofttimesheet.data.model.LoginUser
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

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

// API Interface
interface ApiService {

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