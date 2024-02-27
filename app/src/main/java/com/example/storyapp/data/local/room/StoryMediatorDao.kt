package com.example.storyapp.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.storyapp.data.local.entity.StoryMediatorEntity
import com.example.storyapp.data.response.ListStoryItem


@Dao
interface StoryMediatorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(story: List<StoryMediatorEntity>)

    @Query("SELECT * FROM story_mediator")
    fun getAllStory(): PagingSource<Int, StoryMediatorEntity>

    @Query("DELETE FROM story_mediator")
    suspend fun deleteAll()
}