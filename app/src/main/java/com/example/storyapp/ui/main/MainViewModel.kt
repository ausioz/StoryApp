package com.example.storyapp.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.Repository
import com.example.storyapp.data.local.entity.StoryListEntity
import com.example.storyapp.data.local.room.StoryDatabase
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.data.response.ListStoryItem
import com.example.storyapp.data.response.StoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val repository: Repository, private val application: Application) :
    ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMsg = MutableLiveData<String?>()
    val errorMsg: LiveData<String?> = _errorMsg

    private val _listStory = MutableLiveData<StoryResponse>()
    val listStory: LiveData<StoryResponse> = _listStory

    val db: StoryDatabase = StoryDatabase.getInstance(application)


    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getStory(token: String) {
        _isLoading.value = true
        val client = repository.getStories(token)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        _isLoading.value = false
                        _listStory.value = response.body()

                        viewModelScope.launch(Dispatchers.IO) {
                            db.storyDao().deleteAll()
                            _listStory.value?.listStory?.forEach { story ->
                                db.storyDao().insertList(
                                        StoryListEntity(
                                            0,story.name, story.photoUrl, story.description
                                        )
                                    )
                            }
                        }
                    }
                } else {
                    _isLoading.value = false
                    _errorMsg.value = response.message()
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMsg.value = t.message
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }


    companion object {
        private const val TAG = "MainViewModel"
    }
}