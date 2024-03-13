package com.example.storyapp.data.paging.story

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.data.local.entity.StoryMediatorEntity
import com.example.storyapp.databinding.ItemStoryBinding
import com.example.storyapp.ui.story.detail.DetailStoryActivity

class GetStoryListAdapter:PagingDataAdapter<StoryMediatorEntity, GetStoryListAdapter.ViewHolder>(DIFF_CALLBACK) {
    inner class ViewHolder(private val binding: ItemStoryBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryMediatorEntity) {
            Glide.with(binding.root.context).load(story.photoUrl).into(binding.imageView)
            binding.userTV.text = story.name
            binding.descriptionTV.text = story.description

            binding.cardStory.setOnClickListener {
                val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                intent.putExtra(EXTRA_STORY, story)
                val optionCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.imageView, "imageView"),
                        Pair(binding.userTV, "name"),
                        Pair(binding.descriptionTV, "description")
                    )
                itemView.context.startActivity(intent, optionCompat.toBundle())
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemStoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    companion object {
        const val EXTRA_STORY = "story"

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryMediatorEntity>() {
            override fun areItemsTheSame(
                oldItem: StoryMediatorEntity, newItem: StoryMediatorEntity
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: StoryMediatorEntity, newItem: StoryMediatorEntity
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}