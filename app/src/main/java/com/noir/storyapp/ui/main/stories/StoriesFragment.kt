package com.noir.storyapp.ui.main.stories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.noir.storyapp.R
import com.noir.storyapp.adapters.StoryAdapter
import com.noir.storyapp.data.remote.response.StoriesResponse
import com.noir.storyapp.databinding.FragmentStoriesBinding
import com.noir.storyapp.helper.ViewModelFactory

class StoriesFragment : Fragment() {
    private var _binding: FragmentStoriesBinding? = null
    private val binding get() = _binding

    private val storiesViewModel by viewModels<StoriesViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onResume() {
        super.onResume()
        storiesViewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.isLoggedIn && user.token.isNotEmpty()) {
                storiesViewModel.getStories(user.token)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStoriesBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storiesViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            loaderState(isLoading)
        }

        storiesViewModel.stories.observe(viewLifecycleOwner) { stories ->
            if (stories.listStory.isEmpty()) {
                binding?.tvNoData?.visibility = View.VISIBLE
            } else {
                binding?.tvNoData?.visibility = View.GONE
                populateStories(stories)
            }
        }

        storiesViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error.code == 401) {
                binding?.tvError?.visibility = View.VISIBLE
                binding?.tvError?.text = requireContext().getString(R.string.error, "401 - ${error.message}")
            }
        }
    }

    private fun populateStories(stories: StoriesResponse) {
        val adapter = StoryAdapter()
        adapter.submitList(stories.listStory)
        binding?.rvStories?.adapter = adapter
        binding?.rvStories?.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loaderState(isLoading: Boolean) {
        if (isLoading) binding?.loader?.visibility = View.VISIBLE else binding?.loader?.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}