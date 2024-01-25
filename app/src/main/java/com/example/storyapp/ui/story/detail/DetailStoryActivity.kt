package com.example.storyapp.ui.story.detail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.storyapp.data.response.ListStoryItem
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.ui.main.MainPagerAdapter

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val parcel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(MainPagerAdapter.EXTRA_STORY, ListStoryItem::class.java)
        } else {
            @Suppress("DEPRECATION") intent.getParcelableExtra(MainPagerAdapter.EXTRA_STORY) as? ListStoryItem
        }

        Glide.with(this).load(parcel?.photoUrl).into(binding.imageView)
        binding.userTV.text = parcel?.name
        binding.descriptionTV.text = parcel?.description
    }
}