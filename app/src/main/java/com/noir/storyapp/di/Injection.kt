package com.noir.storyapp.di

import android.content.Context
import com.noir.storyapp.data.Repository
import com.noir.storyapp.data.local.database.StoriesDatabase
import com.noir.storyapp.data.local.pref.UserPreference
import com.noir.storyapp.data.local.pref.dataStore
import com.noir.storyapp.data.remote.retrofit.APIConfig

object Injection {
    fun provideRepository(context: Context): Repository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = APIConfig.getApiService()
        val database = StoriesDatabase.getDatabase(context)
        return Repository.getInstance(apiService, pref, database)
    }
}