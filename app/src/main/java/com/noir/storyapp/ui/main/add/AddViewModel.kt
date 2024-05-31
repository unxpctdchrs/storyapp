package com.noir.storyapp.ui.main.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.noir.storyapp.data.Repository
import com.noir.storyapp.data.pref.UserModel
import com.noir.storyapp.data.remote.response.AddStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddViewModel(private val repository: Repository) : ViewModel() {
    val isLoading: LiveData<Boolean> = repository.addStoryLoading
    val uploadStory: LiveData<AddStoryResponse> = repository.addStory
    fun uploadStory(token: String, file: MultipartBody.Part, description: RequestBody) = repository.uploadStory(token, file, description)

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}