package com.example.mystoryapp.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mystoryapp.data.model.StoryDetail
import com.example.mystoryapp.helper.StoryRepository

class StoryViewModel(private val repository: StoryRepository) : ViewModel() {

//    val stories: LiveData<PagingData<StoryDetail>> = repository.getStories(token).cachedIn(viewModelScope)

    fun getStories(token: String): LiveData<PagingData<StoryDetail>> {
        return repository.getStories(token).cachedIn(viewModelScope)
    }
}

