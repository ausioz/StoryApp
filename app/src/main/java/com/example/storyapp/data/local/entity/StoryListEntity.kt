package com.example.storyapp.data.local.entity


import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
@Entity(tableName = "storylist")
data class StoryListEntity(
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0,
    @ColumnInfo(name = "name")
    var name: String? = null,
    @ColumnInfo(name = "photoUrl")
    var photoUrl: String? = null,
    @ColumnInfo(name = "description")
    var description: String? = null,
) : Parcelable