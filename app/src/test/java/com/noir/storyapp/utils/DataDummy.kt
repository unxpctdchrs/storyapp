package com.noir.storyapp.utils

import com.noir.storyapp.data.remote.response.ListStoryItem

object DataDummy {
    fun generateDummyStoriesResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = ListStoryItem(
                i.toString(),
                "photo $i",
                "createdAt $i",
                "name $i",
                "desc $i",
                i.toDouble(),
                i.toDouble(),
            )
            items.add(quote)
        }
        return items
    }
}