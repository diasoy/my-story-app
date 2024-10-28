package com.example.mystoryapp.view.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mystoryapp.R
import com.example.mystoryapp.data.adapter.StoryAdapter
import com.example.mystoryapp.databinding.ActivityStoryBinding
import com.example.mystoryapp.helper.AppPreferences
import com.example.mystoryapp.view.viewmodel.MainViewModel
import com.example.mystoryapp.view.viewmodel.StoryViewModel
import com.example.mystoryapp.view.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class StoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryBinding
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var userToken: String
    private val pref = AppPreferences.getInstance(dataStore)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)

        userToken = runBlocking { AppPreferences.getInstance(dataStore).getToken().first() }

        storyViewModel = ViewModelProvider(this)[StoryViewModel::class.java]
        storyAdapter = StoryAdapter()

        val rvStory: RecyclerView = findViewById(R.id.rv_stories)
        rvStory.layoutManager = LinearLayoutManager(this)
        rvStory.adapter = storyAdapter

        storyViewModel.stories.observe(this) { stories ->
            storyAdapter.setData(stories)
        }

        storyViewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        storyViewModel.getStories(userToken)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.action_logout -> {
                logoutUser()
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun logoutUser(){
        val dialog = AlertDialog.Builder(this)
        val mainViewModel = ViewModelProvider(this, ViewModelFactory(pref))[MainViewModel::class.java]
        dialog.setTitle("Logout")
        dialog.setMessage("Are you sure want to logout?")
        dialog.setPositiveButton("Yes") { _, _ ->
            mainViewModel.clearDataLogin()
            Toast.makeText(this, "Logout Success", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        dialog.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }

}