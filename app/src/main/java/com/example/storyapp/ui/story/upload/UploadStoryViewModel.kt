package com.example.storyapp.ui.story.upload

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.data.Repository
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.data.response.ErrorResponse
import com.example.storyapp.data.response.FileUploadResponse
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadStoryViewModel(private val repository: Repository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMsg = MutableLiveData<String?>()
    val errorMsg: LiveData<String?> = _errorMsg

    private val _uploadResponse = MutableLiveData<FileUploadResponse>()
    val uploadResponse: LiveData<FileUploadResponse> = _uploadResponse

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun uploadStory(file: MultipartBody.Part, description: RequestBody) {
        _isLoading.value = true
        val client = repository.uploadStory(file, description)
        client.enqueue(object : Callback<FileUploadResponse> {
            override fun onResponse(
                call: Call<FileUploadResponse>, response: Response<FileUploadResponse>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        _isLoading.value = false
                        _uploadResponse.value = response.body()
                    }
                } else {
                    _isLoading.value = false
                    val jsonInString = response.errorBody()?.string()
                    val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                    _errorMsg.value = errorBody.message
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMsg.value = t.message
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    companion object {
        private const val TAG = "AddStoryViewModel"
    }

}