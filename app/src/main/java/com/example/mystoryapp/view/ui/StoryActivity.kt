package com.example.mystoryapp.view.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mystoryapp.R
import com.example.mystoryapp.data.adapter.LoadingStateAdapter
import com.example.mystoryapp.data.adapter.StoryAdapter
import com.example.mystoryapp.data.database.StoryDatabase
import com.example.mystoryapp.data.retrofit.ApiConfig
import com.example.mystoryapp.databinding.ActivityStoryBinding
import com.example.mystoryapp.helper.AppPreferences
import com.example.mystoryapp.helper.StoryRepository
import com.example.mystoryapp.view.viewmodel.MainViewModel
import com.example.mystoryapp.view.viewmodel.StoryViewModel
import com.example.mystoryapp.view.viewmodel.StoryViewModelFactory
import com.example.mystoryapp.view.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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

        storyAdapter = StoryAdapter { story ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_STORY, story)
            startActivity(intent)
        }

        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@StoryActivity)
            adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter { storyAdapter.retry() }
            )
        }

        observeStories()

        binding.pullRefresh.setOnRefreshListener {
            refreshStoryList()
        }

        binding.btnFloating.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeStories() {
        val database = StoryDatabase.getDatabase(this)
        val apiService = ApiConfig.getApiService(userToken)
        val repository = StoryRepository(database, apiService)

        storyViewModel = ViewModelProvider(this, StoryViewModelFactory(repository))[StoryViewModel::class.java]

        lifecycleScope.launch {
            showLoading(true)
            storyViewModel.getStories(userToken).collectLatest { pagingData ->
                storyAdapter.submitData(pagingData)
                showLoading(false)
            }
        }
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
            R.id.action_map -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun refreshStoryList() {
        showLoading(true)
        lifecycleScope.launch {
            storyViewModel.getStories(userToken).collectLatest { pagingData ->
                storyAdapter.submitData(pagingData)
                showLoading(false)
                binding.pullRefresh.isRefreshing = false
            }
        }
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}