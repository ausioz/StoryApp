package com.example.storyapp.utils

import com.example.storyapp.data.local.entity.StoryMediatorEntity

object DataDummy {
    fun generateDummyStoryMediatorEntity(): List<StoryMediatorEntity> {
        val storyList = ArrayList<StoryMediatorEntity>()
        for (i in 0..80) {
            val news = StoryMediatorEntity(
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                "Nama $i",
                "Deskripsi $i",
                -7.10,
                "$i",
                110.10
            )
            storyList.add(news)
        }
        return storyList
    }

//    fun generateDummyStoryResponse():StoryResponse{
//        val storyList = ArrayList<ListStoryItem>()
//        for (i in 0..10) {
//            val news = ListStoryItem(
//                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
//                "2022-02-22T22:22:22Z",
//                "Nama $i",
//                "Deskripsi $i",
//                -7.10,
//                "$i",
//                110.10
//            )
//            storyList.add(news)
//        }
//        return StoryResponse(storyList)
//    }
}