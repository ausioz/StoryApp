package com.example.storyapp.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.data.response.ListStoryItem
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.ui.story.upload.UploadStoryActivity
import com.example.storyapp.ui.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this, application)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainPagerAdapter: MainPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBar)
        binding.recyclerView.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.VERTICAL, false
        )

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.errorMsg.observe(this) {
            showError(it)
        }

        viewModel.getSession().observe(this) {
            if (!it.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
            if (it.isLogin) {
                supportActionBar?.title = getString(R.string.greeting, it.name)
                viewModel.getStory(it.token.toString())
            }
        }

        viewModel.listStory.observe(this) {
            setData(it.listStory)
        }

        binding.fab.setOnClickListener {
            startActivity(Intent(this, UploadStoryActivity::class.java))
        }
    }

    private fun setData(list: List<ListStoryItem>?) {
        mainPagerAdapter = MainPagerAdapter()
        mainPagerAdapter.submitList(list)
        binding.recyclerView.adapter = mainPagerAdapter
    }

    private fun showError(errorMsg: String?) {
        Toast.makeText(this, "Error! \n$errorMsg", Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.apply {
                progressBar.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                toolBar.visibility = View.GONE
            }

        } else {
            binding.apply {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                toolBar.visibility = View.VISIBLE
            }

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

                else -> false
            }
        }

        return super.onCreateOptionsMenu(menu)

    }
}