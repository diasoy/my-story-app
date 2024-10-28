package com.example.mystoryapp.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystoryapp.data.model.ResponseStory
import com.example.mystoryapp.data.model.StoryDetail
import com.example.mystoryapp.data.retrofit.ApiConfig
import kotlinx.coroutines.launch

class StoryViewModel : ViewModel() {

    private val _stories = MutableLiveData<List<StoryDetail>>()
    val stories: LiveData<List<StoryDetail>> = _stories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getStories(token : String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response: ResponseStory = ApiConfig.getApiService(token).fetchAllStories()
                _stories.value = response.listStory
                _errorMessage.value = ""
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
