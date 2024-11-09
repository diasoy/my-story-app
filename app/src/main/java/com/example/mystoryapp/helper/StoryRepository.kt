package com.example.mystoryapp.helper

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.mystoryapp.data.database.StoryDatabase
import com.example.mystoryapp.data.model.StoryDetail
import com.example.mystoryapp.data.retrofit.ApiService
import kotlinx.coroutines.flow.Flow

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getStories(token: String): Flow<PagingData<StoryDetail>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).flow
    }
}
