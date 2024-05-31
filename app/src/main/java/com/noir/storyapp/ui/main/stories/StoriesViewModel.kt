package com.noir.storyapp.ui.main.stories

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.noir.storyapp.data.Repository
import com.noir.storyapp.data.pref.UserModel
import com.noir.storyapp.data.remote.response.Error
import com.noir.storyapp.data.remote.response.StoriesResponse

class StoriesViewModel(private val repository: Repository) : ViewModel() {
    val isLoading: LiveData<Boolean> = repository.storyIsLoading
    val stories: LiveData<StoriesResponse> = repository.stories
    val errorMessage: LiveData<Error> = repository.errorMessage
    fun getStories(token: String) = repository.getStories(token)

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}