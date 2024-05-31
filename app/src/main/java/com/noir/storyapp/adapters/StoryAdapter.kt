package com.noir.storyapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.noir.storyapp.data.remote.response.ListStoryItem
import com.noir.storyapp.databinding.StoryLayoutBinding
import com.noir.storyapp.ui.main.stories.StoriesFragmentDirections

class StoryAdapter() : ListAdapter<ListStoryItem, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {
    inner class ViewHolder(private val binding: StoryLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            ViewCompat.setTransitionName(binding.ivItemPhoto, story.photoUrl)
            ViewCompat.setTransitionName(binding.tvItemName, story.name)
            ViewCompat.setTransitionName(binding.tvItemDescription, story.description)

            Glide.with(binding.root.context)
                .load(story.photoUrl)
                .into(binding.ivItemPhoto)
            binding.tvItemName.text = story.name
            binding.tvItemDescription.text = story.description

            binding.root.setOnClickListener {
                val extras = FragmentNavigatorExtras(
                    binding.ivItemPhoto to "storyImageDetail",
                    binding.tvItemName to "storyNameDetail",
                    binding.tvItemDescription to "storyDescriptionDetail"
                )

                val toDetailStoryFragment = StoriesFragmentDirections.actionNavigationStoriesToDetailStoriesFragment()
                toDetailStoryFragment.name = story.name!!
                toDetailStoryFragment.description = story.description!!
                toDetailStoryFragment.image = story.photoUrl!!
                it.findNavController().navigate(toDetailStoryFragment, extras)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StoryLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>(){
            override fun areItemsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}