package com.noir.storyapp.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.noir.storyapp.data.Repository
import com.noir.storyapp.data.remote.response.RegisterResponse

class SignUpViewModel(private val repository: Repository) : ViewModel() {
    val isLoading: LiveData<Boolean> = repository.isLoading
    val register: LiveData<RegisterResponse> = repository.register

    fun register(name: String, email: String, password: String) = repository.register(name, email, password)
}