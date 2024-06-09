package com.noir.storyapp.data.local.pref

data class UserModel(
    val userId: String,
    val name: String,
    val token: String,
    val isLoggedIn: Boolean = false
)