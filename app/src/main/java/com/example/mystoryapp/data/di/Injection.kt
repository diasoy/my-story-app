package com.example.mystoryapp.data.di

import android.content.Context
import com.example.mystoryapp.data.repository.StoryRepository
import com.example.mystoryapp.data.retrofit.ApiConfig
import com.example.mystoryapp.helper.AppPreferences
import com.example.mystoryapp.view.ui.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = AppPreferences.getInstance(context.dataStore)
        val token = runBlocking { pref.getToken().first() }
        val apiService = ApiConfig.getApiService(token)
        return StoryRepository(apiService)
    }
}