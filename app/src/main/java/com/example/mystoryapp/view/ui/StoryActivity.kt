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
import com.example.mystoryapp.R
import com.example.mystoryapp.data.adapter.StoryAdapter
import com.example.mystoryapp.databinding.ActivityStoryBinding
import com.example.mystoryapp.helper.AppPreferences
import com.example.mystoryapp.view.viewmodel.MainViewModel
import com.example.mystoryapp.view.viewmodel.StoryViewModel
import com.example.mystoryapp.view.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@Suppress("NAME_SHADOWING")
class StoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryBinding
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var userToken: String
    private lateinit var pref: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.app_name)

        pref = AppPreferences.getInstance(applicationContext.dataStore)
        userToken = runBlocking { pref.getToken().first() }
        storyViewModel = ViewModelProvider(this)[StoryViewModel::class.java]
        storyAdapter = StoryAdapter { story ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_STORY, story)
            startActivity(intent)
        }

        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@StoryActivity)
            adapter = storyAdapter
        }

        binding.pullRefresh.setOnRefreshListener {
            refreshStoryList()
        }

        storyViewModel.stories.observe(this) { stories ->
            storyAdapter.setData(stories)
            binding.pullRefresh.isRefreshing = false

        }

        storyViewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
            binding.pullRefresh.isRefreshing = false

        }

        binding.btnFloating.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun refreshStoryList() {
        binding.pullRefresh.isRefreshing = true
        storyViewModel.getStories(userToken)
    }

    private fun logoutUser(){
        val dialog = AlertDialog.Builder(this)
        val mainViewModel = ViewModelProvider(this, ViewModelFactory(pref))[MainViewModel::class.java]
        dialog.setTitle("Logout")
        dialog.setMessage("Apakah anda yakin ingin keluar dari akun?")
        dialog.setPositiveButton("Ya") { _, _ ->
            mainViewModel.clearDataLogin()
            Toast.makeText(this, "Logout Sukses", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        dialog.setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }
}