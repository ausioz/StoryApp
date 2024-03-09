package com.example.storyapp.ui.user.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.RepositoryImpl
import com.example.storyapp.data.response.ErrorResponse
import com.example.storyapp.data.response.RegisterResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(private val repositoryImpl: RepositoryImpl) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMsg = MutableLiveData<String?>()
    val errorMsg: LiveData<String?> = _errorMsg

    private val _registerData = MutableLiveData<RegisterResponse>()
    val registerData: LiveData<RegisterResponse> = _registerData


    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        val client = repositoryImpl.register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>, response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        _isLoading.value = false
                        _registerData.value = response.body()
                    }
                } else {
                    _isLoading.value = false
                    val jsonInString = response.errorBody()?.string()
                    val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                    _errorMsg.value = errorBody.message

                    Log.e(TAG, "onFailure: ${response.message()}")

                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMsg.value = t.message
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    companion object {
        private const val TAG = "RegisterViewModel"
    }
}