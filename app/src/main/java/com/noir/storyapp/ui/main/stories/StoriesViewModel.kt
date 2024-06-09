package com.noir.storyapp.ui.main.stories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.noir.storyapp.data.Repository
import com.noir.storyapp.data.local.pref.UserModel
import com.noir.storyapp.data.remote.response.Error
import com.noir.storyapp.data.remote.response.ListStoryItem

class StoriesViewModel(private val repository: Repository) : ViewModel() {
    val hasScrolledToTop = MutableLiveData(false)
    private val _token = MutableLiveData<String>()
    private val _stories = MutableLiveData<LiveData<PagingData<ListStoryItem>>>()

    val stories: LiveData<PagingData<ListStoryItem>> = _stories.switchMap { liveData -> liveData }
    val errorMessage: LiveData<Error> = repository.errorMessage
    private val session: LiveData<UserModel> = repository.getSession().asLiveData()

    init {
        session.observeForever { user ->
            user?.let {
                _token.value = it.token
                _stories.value = repository.getStories(it.token).cachedIn(viewModelScope)
            }
        }
    }

    fun refreshStories() {
        session.value?.let { user ->
            _token.value = user.token
            _stories.value = repository.getStories(user.token).cachedIn(viewModelScope)
        }
    }
}