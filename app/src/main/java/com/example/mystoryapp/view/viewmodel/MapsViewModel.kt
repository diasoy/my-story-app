package com.example.mystoryapp.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystoryapp.data.model.ResponseStory
import com.example.mystoryapp.data.model.StoryDetail
import com.example.mystoryapp.data.retrofit.ApiConfig
import kotlinx.coroutines.launch

class MapsViewModel() : ViewModel() {
    private val _location = MutableLiveData<List<StoryDetail>>()
    val location: LiveData<List<StoryDetail>> = _location

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getLocation(token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response: ResponseStory = ApiConfig.getApiService(token).getStoriesWithLocation()
                _location.value = response.listStory
                _errorMessage.value = ""
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error occurred"
            }finally {
                _isLoading.value = false
            }

        }
    }

}