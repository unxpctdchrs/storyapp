package com.noir.storyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.noir.storyapp.data.local.database.StoriesDatabase
import com.noir.storyapp.data.local.pref.UserModel
import com.noir.storyapp.data.local.pref.UserPreference
import com.noir.storyapp.data.remote.response.AddStoryResponse
import com.noir.storyapp.data.remote.response.Error
import com.noir.storyapp.data.remote.response.ListStoryItem
import com.noir.storyapp.data.remote.response.LoginResponse
import com.noir.storyapp.data.remote.response.RegisterResponse
import com.noir.storyapp.data.remote.StoriesRemoteMediator
import com.noir.storyapp.data.remote.response.StoriesResponse
import com.noir.storyapp.data.remote.retrofit.APIService
import com.noir.storyapp.helper.wrapEspressoIdlingResource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository private constructor(
    private val apiService: APIService,
    private val userPreference: UserPreference,
    private val storiesDatabase: StoriesDatabase
) {

    private val _register = MutableLiveData<RegisterResponse>()
    val register: LiveData<RegisterResponse> = _register

    private val _login = MutableLiveData<LoginResponse>()
    val login: LiveData<LoginResponse> = _login

    private val _stories = MutableLiveData<StoriesResponse>()
    val stories: LiveData<StoriesResponse> = _stories

    private val _addStory = MutableLiveData<AddStoryResponse>()
    val addStory: LiveData<AddStoryResponse> =_addStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _addStoryLoading = MutableLiveData<Boolean>()
    val addStoryLoading: LiveData<Boolean> = _addStoryLoading

    private val _errorMessage = MutableLiveData<Error> ()
    val errorMessage: LiveData<Error> = _errorMessage

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        val client = apiService.register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _register.value = response.body()
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        val client = apiService.login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _login.value = response.body()
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun getStories(token: String) : LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoriesRemoteMediator(token = token, storiesDatabase = storiesDatabase, apiService = apiService),
            pagingSourceFactory = {
                storiesDatabase.storiesDAO().getStories()
            }
        ).liveData
    }

    fun getStoriesForMap(): LiveData<List<ListStoryItem>> = storiesDatabase.storiesDAO().getStoriesForMap()

    fun uploadStory(token: String, file: MultipartBody.Part, description: RequestBody, lat: RequestBody? = null, lon: RequestBody? = null) {
        wrapEspressoIdlingResource {
            _addStoryLoading.value = true
            val client = apiService.uploadStory("Bearer $token", file, description, lat, lon)
            client.enqueue(object : Callback<AddStoryResponse> {
                override fun onResponse(
                    call: Call<AddStoryResponse>,
                    response: Response<AddStoryResponse>
                ) {
                    _addStoryLoading.value = false
                    if (response.isSuccessful) {
                        _addStory.value = response.body()
                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                    _addStoryLoading.value = false
                    Log.e(TAG, "onFailure: ${t.message.toString()}")
                }
            })
        }
    }

    companion object {
        private const val TAG = "Repository"

        @Volatile
        private var instance: Repository? = null

        fun getInstance(apiService: APIService, userPreference: UserPreference, storiesDatabase: StoriesDatabase) : Repository = instance ?: synchronized(this) {
            instance ?: synchronized(this) {
                instance ?: Repository(apiService, userPreference, storiesDatabase)
            }.also { instance = it }
        }
    }
}