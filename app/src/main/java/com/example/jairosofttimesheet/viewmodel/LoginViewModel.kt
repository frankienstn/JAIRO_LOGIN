package com.example.jairosofttimesheet.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jairosofttimesheet.data.model.LoginRequest
import com.example.jairosofttimesheet.data.remote.ApiService
import com.example.jairosofttimesheet.data.remote.retrofit
import com.example.jairosofttimesheet.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginViewModel : ViewModel() {
    private val apiService = retrofit.create(ApiService::class.java)
    private val repository = Repository(apiService)

    private val _loginMessage = MutableStateFlow<String?>(null)
    val loginMessage: StateFlow<String?> = _loginMessage

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    fun login(email: String, password: String) {
        val request = LoginRequest(email, password)
        viewModelScope.launch {
            try {
                repository.loginUser(request) { success, message ->
                    _loginSuccess.value = success
                    _loginMessage.value = message
                }
            } catch (e: IOException) {
                Log.e("LoginViewModel", "Network error: ${e.message}", e)
                _loginSuccess.value = false
                _loginMessage.value = "Network error. Please check your connection."
            } catch (e: HttpException) {
                Log.e("LoginViewModel", "HTTP error ${e.code()}: ${e.message()}", e)
                _loginSuccess.value = false
                _loginMessage.value = "Server error. Please try again later."
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Unexpected error: ${e.message}", e)
                _loginSuccess.value = false
                _loginMessage.value = "Unexpected error occurred."
            }
        }
    }

    fun clearMessage() {
        _loginMessage.value = null
    }
}
