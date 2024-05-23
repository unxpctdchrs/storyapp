package com.noir.storyapp.di

import android.content.Context
import com.noir.storyapp.data.Repository
import com.noir.storyapp.data.pref.UserPreference
import com.noir.storyapp.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): Repository {
        val pref = UserPreference.getInstance(context.dataStore)
        return Repository.getInstance(pref)
    }
}