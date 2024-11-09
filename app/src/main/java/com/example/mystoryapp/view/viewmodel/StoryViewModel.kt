package com.example.mystoryapp.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mystoryapp.data.model.ResponseStory
import com.example.mystoryapp.data.model.StoryDetail
import com.example.mystoryapp.data.retrofit.ApiConfig
import com.example.mystoryapp.helper.StoryRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow

class StoryViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _stories = MutableLiveData<List<StoryDetail>>()
    val stories: LiveData<List<StoryDetail>> = _stories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getStories(token: String): Flow<PagingData<StoryDetail>> {
        return repository.getStories(token).cachedIn(viewModelScope)
    }
}

