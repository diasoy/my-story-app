package com.example.mystoryapp.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mystoryapp.data.model.StoryDetail

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<StoryDetail>)

    @Query("SELECT * FROM stories")
    fun getAllStory(): PagingSource<Int, StoryDetail>

    @Query("DELETE FROM stories")
    suspend fun deleteAll()
}