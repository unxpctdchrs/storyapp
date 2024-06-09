package com.noir.storyapp.data.local.database

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.noir.storyapp.data.remote.response.ListStoryItem

@Dao
interface StoriesDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<ListStoryItem>)

    @Query("SELECT * FROM stories")
    fun getStories(): PagingSource<Int, ListStoryItem>

    @Query("SELECT * FROM stories WHERE lon IS NOT NULL AND lat IS NOT NULL")
    fun getStoriesForMap(): LiveData<List<ListStoryItem>>

    @Query("DELETE FROM stories")
    suspend fun deleteStories()
}