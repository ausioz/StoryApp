package com.example.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.data.paging.LoadingStateAdapter
import com.example.storyapp.data.paging.story.GetStoryListAdapter
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.di.Injection
import com.example.storyapp.ui.map.StoryMapsActivity
import com.example.storyapp.ui.story.upload.UploadStoryActivity
import com.example.storyapp.ui.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this, application)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var getStoryListAdapter: GetStoryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBar)
        binding.recyclerView.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.VERTICAL, false
        )

        viewModel.getSession().observe(this) {
            if (!it.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
            if (it.isLogin) {
                supportActionBar?.title = getString(R.string.greeting, it.name)
                getStoryData()
            }
        }

        binding.fab.setOnClickListener {
            startActivity(Intent(this, UploadStoryActivity::class.java))
        }
    }

//    override fun onResume() {
//        super.onResume()
//        getStoryData()
//    }

    private fun getStoryData() {
        getStoryListAdapter = GetStoryListAdapter()
        binding.recyclerView.adapter =
            getStoryListAdapter.withLoadStateFooter(footer = LoadingStateAdapter {
                getStoryListAdapter.retry()
            })
        viewModel.getStory().observe(this) {
            getStoryListAdapter.submitData(lifecycle, it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        binding.toolBar.inflateMenu(R.menu.option_menu)
        binding.toolBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> {
                    viewModel.logout()
                    true
                }
                R.id.maps -> {
                    startActivity(Intent(this, StoryMapsActivity::class.java))
                    true
                }
                else -> false
            }
        }
        return super.onCreateOptionsMenu(menu)

    }
}