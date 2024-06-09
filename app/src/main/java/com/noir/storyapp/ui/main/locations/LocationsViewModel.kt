package com.noir.storyapp.ui.main.locations

import androidx.lifecycle.ViewModel
import com.noir.storyapp.data.Repository

class LocationsViewModel(private val repository: Repository) : ViewModel() {
    fun getStoriesForMap() = repository.getStoriesForMap()
}