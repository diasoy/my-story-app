package com.example.mystoryapp.view.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.mystoryapp.data.model.StoryDetail
import com.example.mystoryapp.databinding.ActivityDetailBinding

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story = intent.getParcelableExtra<StoryDetail>(EXTRA_STORY)
        if (story != null) {
            binding.apply {
                storyTitle.text = story.name
                storyDescription.text = story.description
                Glide.with(this@DetailActivity)
                    .load(story.photoUrl)
                    .into(storyImage)

                val scaleX = ObjectAnimator.ofFloat(storyImage, "scaleX", 1.2f, 1f).setDuration(1000)
                val scaleY = ObjectAnimator.ofFloat(storyImage, "scaleY", 1.2f, 1f).setDuration(1000)
                val fadeIn = ObjectAnimator.ofFloat(storyImage, "alpha", 0f, 1f).setDuration(1000)

                AnimatorSet().apply {
                    playTogether(scaleX, scaleY, fadeIn)
                    start()
                }
            }
        }
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}
