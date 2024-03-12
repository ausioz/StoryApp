package com.example.storyapp.data

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.data.local.entity.StoryListEntity
import com.example.storyapp.data.local.entity.StoryMediatorEntity
import com.example.storyapp.data.local.room.StoryDatabase
import com.example.storyapp.data.local.room.StoryMediatorDatabase
import com.example.storyapp.data.paging.story.GetStoryMediator
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.data.pref.UserPreference
import com.example.storyapp.data.remote.ApiService
import com.example.storyapp.data.response.FileUploadResponse
import com.example.storyapp.data.response.LoginResponse
import com.example.storyapp.data.response.RegisterResponse
import com.example.storyapp.data.response.StoryResponse
import com.example.storyapp.util.wrapEspressoIdlingResource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call

open class RepositoryImpl(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
    private val storyDatabase:StoryDatabase,
    private val storyMediatorDatabase: StoryMediatorDatabase
):Repository {

    override suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    override fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    override suspend fun logout() {
        userPreference.logout()
    }

    override fun login(email: String, password: String): Call<LoginResponse> {
        wrapEspressoIdlingResource {
            return apiService.login(email, password)
        }
    }

    override fun register(name: String, email: String, password: String): Call<RegisterResponse> {
        return apiService.register(name, email, password)
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getStories(): LiveData<PagingData<StoryMediatorEntity>> {
        return Pager(config = PagingConfig(pageSize = 20),
            remoteMediator = GetStoryMediator(storyMediatorDatabase, apiService),
            pagingSourceFactory = {
            storyMediatorDatabase.storyMediatorDao().getAllStory()
        }).liveData
    }

    override fun getStoriesWithLocation(): Call<StoryResponse> {
        return apiService.getStoriesWithLocation()
    }

    override fun deleteStoriesToDatabase() {
        return storyDatabase.storyDao().deleteAll()
    }
    override fun saveStoriesToDatabase(story:StoryListEntity) {
        return storyDatabase.storyDao().insertList(story)
    }


    override fun uploadStory(
        file: MultipartBody.Part, description: RequestBody, lat: Float?, long: Float?
    ): Call<FileUploadResponse> {
        return apiService.uploadStory(file, description, lat, long)
    }


    companion object {
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
            storyDatabase:StoryDatabase,
            storyMediatorDatabase: StoryMediatorDatabase
        ): RepositoryImpl = synchronized(this) {
            RepositoryImpl(userPreference, apiService,storyDatabase, storyMediatorDatabase)
        }
    }
}