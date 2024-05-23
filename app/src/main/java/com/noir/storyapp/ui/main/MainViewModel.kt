package com.noir.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.noir.storyapp.data.Repository
import com.noir.storyapp.data.pref.UserModel
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}