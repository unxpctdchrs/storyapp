package com.noir.storyapp.ui.main.stories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.noir.storyapp.adapters.StoryAdapter
import com.noir.storyapp.data.Repository
import com.noir.storyapp.data.local.pref.UserModel
import com.noir.storyapp.data.remote.response.ListStoryItem
import com.noir.storyapp.utils.DataDummy
import com.noir.storyapp.utils.LiveDataTestUtil.getOrAwaitValue
import com.noir.storyapp.utils.MainDispatcherRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoriesViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var repository: Repository

    @Test
    fun `when Get Stories Should Not Null and Return Data`() = runTest {
        val dummyData = DataDummy.generateDummyStoriesResponse()
        val dummyPagingData = PagingData.from(dummyData)
        val dummyUser = UserModel("dummyId", "dummyName", "dummyToken", true)
        Mockito.`when`(repository.getSession()).thenReturn(flowOf(dummyUser))

        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = dummyPagingData
        Mockito.`when`(repository.getStories(dummyUser.token)).thenReturn(expectedStories)

        val viewModel = StoriesViewModel(repository)
        val actualStories = viewModel.stories.getOrAwaitValue()

        Assert.assertNotNull(actualStories)
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)
        Assert.assertEquals(dummyData.size, differ.snapshot().size)
        Assert.assertEquals(dummyData[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Stories is Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val dummyUser = UserModel("dummyId", "dummyName", "dummyToken", true)
        Mockito.`when`(repository.getSession()).thenReturn(flowOf(dummyUser))

        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = data
        Mockito.`when`(repository.getStories(dummyUser.token)).thenReturn(expectedStories)

        val viewModel = StoriesViewModel(repository)
        val actualStories = viewModel.stories.getOrAwaitValue()

        Assert.assertNotNull(actualStories)
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)
        Assert.assertEquals(0, differ.snapshot().size)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}

class StoriesPagingSource: PagingSource<Int, LiveData<List<ListStoryItem>>>() {
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }

    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }
}