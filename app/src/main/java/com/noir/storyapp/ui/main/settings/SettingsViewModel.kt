package com.noir.storyapp.ui.main.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noir.storyapp.data.Repository
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: Repository) : ViewModel() {
    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}