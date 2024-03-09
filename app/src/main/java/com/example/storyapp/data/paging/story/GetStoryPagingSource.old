package com.example.storyapp.data.paging.story

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyapp.data.remote.ApiService
import com.example.storyapp.data.response.ListStoryItem

class `GetStoryPagingSource-old`(private val apiService: ApiService) :
    PagingSource<Int, ListStoryItem>() {
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: 1
            val responseData = apiService.getStories(position,params.loadSize)
            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (responseData.listStory.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            Log.d("GetStoryPagingSource", "load: $exception")
            return LoadResult.Error(exception)
        }
    }
}