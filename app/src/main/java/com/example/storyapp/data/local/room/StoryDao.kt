package com.example.storyapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.storyapp.data.local.entity.StoryListEntity

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.NONE)
    fun insertList(story: StoryListEntity)

    @Query("Delete FROM storylist")
    fun deleteAll()

    @Query("SELECT * FROM storylist")
    fun getList(): LiveData<List<StoryListEntity>>

}