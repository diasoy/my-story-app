package com.example.mystoryapp.data.repository

import com.example.mystoryapp.data.model.ResponseDetail
import com.example.mystoryapp.data.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(private val apiService: ApiService) {

    suspend fun getStoryDetail(storyId: String): ResponseDetail {
        return apiService.fetchDetailStory( storyId)
    }

    suspend fun uploadStory(photo: MultipartBody.Part, des: RequestBody): ResponseDetail {
        return apiService.uploadStory(photo, des)
    }
}