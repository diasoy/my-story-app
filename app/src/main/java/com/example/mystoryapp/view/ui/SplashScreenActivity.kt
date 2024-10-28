package com.example.mystoryapp.view.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.databinding.ActivitySplashScreenBinding
import com.example.mystoryapp.helper.AppPreferences
import com.example.mystoryapp.view.viewmodel.MainViewModel
import com.example.mystoryapp.view.viewmodel.ViewModelFactory

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val pref = AppPreferences.getInstance(dataStore)
        val loginViewModel = ViewModelProvider(this, ViewModelFactory(pref))[MainViewModel::class.java]

        loginViewModel.getLoginSession().observe(this) { isLoggedIn ->

            val splashScreenLogo = ObjectAnimator.ofFloat(binding.imageView, View.ALPHA, 1f).setDuration(3000)

            AnimatorSet().apply {
                playSequentially(splashScreenLogo)
                start()
            }

            val intent = if (isLoggedIn) {
                Intent(this, StoryActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }

            binding.imageView.animate()
                .setDuration(5000)
                .alpha(0f)
                .withEndAction {
                    startActivity(intent)
                    finish()
                }
        }
    }
}