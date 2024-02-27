package com.example.storyapp.data

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.data.local.entity.StoryMediatorEntity
import com.example.storyapp.data.local.room.StoryMediatorDatabase
import com.example.storyapp.data.paging.story.GetStoryMediator
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.data.pref.UserPreference
import com.example.storyapp.data.remote.ApiService
import com.example.storyapp.data.response.FileUploadResponse
import com.example.storyapp.data.response.ListStoryItem
import com.example.storyapp.data.response.LoginResponse
import com.example.storyapp.data.response.RegisterResponse
import com.example.storyapp.data.response.StoryResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call

class Repository private constructor(
    private val userPreference: UserPreference, private val apiService: ApiService,
    private val storyMediatorDatabase: StoryMediatorDatabase
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun login(email: String, password: String): Call<LoginResponse> {
        return apiService.login(email, password)
    }

    fun register(name: String, email: String, password: String): Call<RegisterResponse> {
        return apiService.register(name, email, password)
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getStories(): LiveData<PagingData<StoryMediatorEntity>> {
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = GetStoryMediator(storyMediatorDatabase, apiService),
            pagingSourceFactory = {
                storyMediatorDatabase.storyMediatorDao().getAllStory()
            }).liveData
    }

    fun getStoriesWithLocation(): Call<StoryResponse> {
        return apiService.getStoriesWithLocation()
    }

    fun uploadStory(
        file: MultipartBody.Part, description: RequestBody
    ): Call<FileUploadResponse> {
        return apiService.uploadStory(file, description)
    }

    companion object {
        fun getInstance(
            userPreference: UserPreference, apiService: ApiService, storyMediatorDatabase: StoryMediatorDatabase
        ): Repository = synchronized(this) {
            Repository(userPreference, apiService,storyMediatorDatabase)
        }
    }
}