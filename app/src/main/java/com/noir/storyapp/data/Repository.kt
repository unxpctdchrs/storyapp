package com.noir.storyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.noir.storyapp.data.pref.UserModel
import com.noir.storyapp.data.pref.UserPreference
import com.noir.storyapp.data.remote.response.LoginResponse
import com.noir.storyapp.data.remote.response.RegisterResponse
import com.noir.storyapp.data.remote.retrofit.APIConfig
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository private constructor(private val userPreference: UserPreference) {
    private val _register = MutableLiveData<RegisterResponse>()
    val register: LiveData<RegisterResponse> = _register

    private val _login = MutableLiveData<LoginResponse>()
    val login: LiveData<LoginResponse> = _login

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

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
        val client = APIConfig.getApiService().register(name, email, password)
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
        val client = APIConfig.getApiService().login(email, password)
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

    companion object {
        private const val TAG = "Repository"

        @Volatile
        private var instance: Repository? = null

        fun getInstance(userPreference: UserPreference) : Repository = instance ?: synchronized(this) {
            instance ?: synchronized(this) {
                instance ?: Repository(userPreference)
            }.also { instance = it }
        }
    }
}