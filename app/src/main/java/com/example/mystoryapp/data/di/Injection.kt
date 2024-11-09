package com.example.mystoryapp.data.di

import android.content.Context
import com.example.mystoryapp.data.database.StoryDatabase
import com.example.mystoryapp.data.retrofit.ApiConfig
import com.example.mystoryapp.helper.StoryRepository

object Injection {
    fun provideRepository(context: Context, token: String): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService(token)
        return StoryRepository(database, apiService)
    }
}