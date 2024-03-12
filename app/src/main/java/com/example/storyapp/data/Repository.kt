package com.example.storyapp.data

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.example.storyapp.data.local.entity.StoryListEntity
import com.example.storyapp.data.local.entity.StoryMediatorEntity
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.data.response.FileUploadResponse
import com.example.storyapp.data.response.LoginResponse
import com.example.storyapp.data.response.RegisterResponse
import com.example.storyapp.data.response.StoryResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call

interface Repository {

    suspend fun saveSession(user: UserModel)
    fun getSession(): Flow<UserModel>
    suspend fun logout()
    fun login(email: String, password: String): Call<LoginResponse>
    fun register(name: String, email: String, password: String): Call<RegisterResponse>
    fun getStories(): LiveData<PagingData<StoryMediatorEntity>>
    fun getStoriesWithLocation(): Call<StoryResponse>
    fun deleteStoriesToDatabase()
    fun saveStoriesToDatabase(story: StoryListEntity)
    fun uploadStory(
        file: MultipartBody.Part, description: RequestBody, lat: Float?, long: Float?
    ): Call<FileUploadResponse>

}