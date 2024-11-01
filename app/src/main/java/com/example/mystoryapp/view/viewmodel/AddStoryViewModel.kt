package com.example.mystoryapp.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystoryapp.data.retrofit.ApiConfig
import com.example.mystoryapp.helper.AppPreferences
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val pref: AppPreferences) : ViewModel() {
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isUploaded = MutableLiveData<Boolean>()
    val isUploaded: LiveData<Boolean> = _isUploaded

    fun uploadStory(photo: MultipartBody.Part, des: RequestBody, token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService(token).uploadStory(photo, des)
                _isLoading.value = false
                _message.value = "Story berhasil di upload!"
                _isUploaded.value = true
            } catch (e: Exception) {
                _message.value = e.message
                _isUploaded.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
}
