package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.data.RepositoryImpl
import com.example.storyapp.data.local.room.StoryDatabase
import com.example.storyapp.data.local.room.StoryMediatorDatabase
import com.example.storyapp.data.pref.UserPreference
import com.example.storyapp.data.pref.dataStore
import com.example.storyapp.data.remote.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): RepositoryImpl {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        val storyDatabase = StoryDatabase.getInstance(context)
        val storyMediatorDatabase = StoryMediatorDatabase.getDatabase(context)
        return RepositoryImpl.getInstance(pref, apiService,storyDatabase, storyMediatorDatabase)
    }
}