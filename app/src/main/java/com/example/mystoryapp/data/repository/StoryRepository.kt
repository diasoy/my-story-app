package com.example.mystoryapp.data.repository

import com.example.mystoryapp.data.model.ResponseDetail
import com.example.mystoryapp.data.retrofit.ApiService

class StoryRepository(private val apiService: ApiService) {

    suspend fun getStoryDetail(token: String, storyId: String): ResponseDetail {
        return apiService.fetchDetailStory(token, storyId)
    }
}