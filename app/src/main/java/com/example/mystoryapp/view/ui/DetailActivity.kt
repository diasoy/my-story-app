// DetailActivity.kt
package com.example.mystoryapp.view.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.mystoryapp.data.model.StoryDetail
import com.example.mystoryapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story = intent.getParcelableExtra<StoryDetail>(EXTRA_STORY)
        if (story != null) {
            binding.apply {
                binding.storyTitle.text = story.name
                binding.storyDescription.text = story.description
                Glide.with(this@DetailActivity)
                    .load(story.photoUrl)
                    .into(binding.storyImage)

            }
        }
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}
