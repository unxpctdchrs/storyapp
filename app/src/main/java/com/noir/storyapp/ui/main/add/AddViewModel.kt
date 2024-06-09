package com.noir.storyapp.ui.main.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.noir.storyapp.data.Repository
import com.noir.storyapp.data.local.pref.UserModel
import com.noir.storyapp.data.remote.response.AddStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddViewModel(private val repository: Repository) : ViewModel() {
    val isLoading: LiveData<Boolean> = repository.addStoryLoading
    val uploadStory: LiveData<AddStoryResponse> = repository.addStory
    fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ) = repository.uploadStory(token, file, description, lat, lon)

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}