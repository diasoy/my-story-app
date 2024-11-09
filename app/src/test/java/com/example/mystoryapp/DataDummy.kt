package com.example.mystoryapp

import com.example.mystoryapp.data.model.StoryDetail

object DataDummy {
    fun generateDummyStories(): List<StoryDetail> {
        val stories: MutableList<StoryDetail> = arrayListOf()
        for (i in 0..10) {
            val story = StoryDetail(
                id = "story-$i",
                name = "Story $i",
                description = "Description $i",
                photoUrl = "https://example.com/photo/$i",
                createdAt = "2021-09-09T00:00:00Z",
                lat = 0.0,
                lon = 0.0
            )
            stories.add(story)
        }
        return stories
    }
}