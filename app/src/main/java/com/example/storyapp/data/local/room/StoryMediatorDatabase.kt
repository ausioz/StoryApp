package com.example.storyapp.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.storyapp.data.local.entity.StoryMediatorEntity

@Database(
    entities = [StoryMediatorEntity::class, StoryRemoteKeys::class],
    version = 2,
    exportSchema = false
)
abstract class StoryMediatorDatabase : RoomDatabase() {
    abstract fun storyMediatorDao(): StoryMediatorDao
    abstract fun storyRemoteKeysDao(): StoryRemoteKeysDao

    companion object {
        @Volatile
        private var instance: StoryMediatorDatabase? = null
        @JvmStatic
        fun getDatabase(context: Context): StoryMediatorDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryMediatorDatabase::class.java, "quote_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }
    }
}