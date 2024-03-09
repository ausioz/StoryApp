package com.example.storyapp.ui.main

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.test.core.app.ApplicationProvider
import com.example.storyapp.data.RepositoryImpl
import com.example.storyapp.data.local.entity.StoryMediatorEntity
import com.example.storyapp.data.paging.story.GetStoryListAdapter
import com.example.storyapp.utils.DataDummy
import com.example.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repositoryImpl: RepositoryImpl
    private lateinit var mainViewModel: MainViewModel
    private val dummyStories = DataDummy.generateDummyStoryMediatorEntity()
    private val pagingDataDiffer = AsyncPagingDataDiffer(
        diffCallback = GetStoryListAdapter.DIFF_CALLBACK,
        updateCallback = ListUpdateTestCallback(),
        workerDispatcher = Dispatchers.Main
    )

    @Before
    fun setUp() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        mainViewModel = MainViewModel(repositoryImpl, application)

    }

    @Test
    fun storiesShouldBeReturnedWhenItsNotNull() {
        runTest {
            val expectedStories = MutableLiveData<PagingData<StoryMediatorEntity>>()
            expectedStories.value = PagingData.from(dummyStories)
            `when`(repositoryImpl.getStories()).thenReturn(expectedStories)

            val actualStories = mainViewModel.getStory().getOrAwaitValue()
            Mockito.verify(repositoryImpl).getStories()

            val expectedDiffer = pagingDataDiffer
            expectedDiffer.submitData(expectedStories.getOrAwaitValue())
            val actualDiffer = pagingDataDiffer
            actualDiffer.submitData(actualStories)

            //Memastikan data tidak null.
            assertNotNull(actualDiffer.snapshot().items)
            //Memastikan jumlah data sesuai dengan yang diharapkan.
            assertEquals(
                expectedDiffer.snapshot().items.size, actualDiffer.snapshot().items.size
            )
            //Memastikan data pertama yang dikembalikan sesuai.
            assertEquals(
                expectedDiffer.snapshot().items.first(), actualDiffer.snapshot().items.first()
            )
        }
    }

    class ListUpdateTestCallback : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}

        override fun onRemoved(position: Int, count: Int) {}

        override fun onMoved(fromPosition: Int, toPosition: Int) {}

        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}


