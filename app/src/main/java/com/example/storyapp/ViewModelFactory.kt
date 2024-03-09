package com.example.storyapp

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.RepositoryImpl
import com.example.storyapp.di.Injection
import com.example.storyapp.ui.main.MainViewModel
import com.example.storyapp.ui.story.upload.UploadStoryViewModel
import com.example.storyapp.ui.user.login.LoginViewModel
import com.example.storyapp.ui.user.register.RegisterViewModel

class ViewModelFactory(private val repositoryImpl: RepositoryImpl, private val application: Application) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repositoryImpl, application) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repositoryImpl) as T
            }

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(repositoryImpl) as T
            }

            modelClass.isAssignableFrom(UploadStoryViewModel::class.java) -> {
                UploadStoryViewModel(repositoryImpl) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        fun getInstance(context: Context, application: Application): ViewModelFactory {
              return synchronized(ViewModelFactory::class.java) {
                        ViewModelFactory(Injection.provideRepository(context), application)
                }
        }
    }
}