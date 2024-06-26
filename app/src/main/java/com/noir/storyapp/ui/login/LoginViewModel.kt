package com.noir.storyapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.noir.storyapp.data.Repository
import com.noir.storyapp.data.local.pref.UserModel
import com.noir.storyapp.data.remote.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: Repository) : ViewModel() {
    val login: LiveData<LoginResponse> = repository.login
    val isLoading: LiveData<Boolean> = repository.isLoading

    fun login(email: String, password: String) = repository.login(email, password)

    fun saveSession(userModel: UserModel) {
        viewModelScope.launch {
            repository.saveSession(userModel)
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}