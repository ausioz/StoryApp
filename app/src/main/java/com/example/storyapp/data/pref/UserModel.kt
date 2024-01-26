package com.example.storyapp.data.pref

data class UserModel(
    val email: String?,
    val name: String?,
    val token: String?,
    val isLogin: Boolean = false
)