package com.noir.storyapp.ui.main.stories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.noir.storyapp.R
import com.noir.storyapp.adapters.LoadingStateAdapter
import com.noir.storyapp.adapters.StoryAdapter
import com.noir.storyapp.databinding.FragmentStoriesBinding
import com.noir.storyapp.helper.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StoriesFragment : Fragment() {
    private var _binding: FragmentStoriesBinding? = null
    private val binding get() = _binding

    private val storiesViewModel by viewModels<StoriesViewModel> {
        ViewModelFactory.getInstance(requireContext())
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

        populateStories()

        storiesViewModel.hasScrolledToTop.observe(viewLifecycleOwner) { hasScrolled ->
            Log.d("hasScrolledToTop", hasScrolled.toString())
        }

        binding?.swipeRefreshLayout?.setOnRefreshListener {
            binding?.swipeRefreshLayout?.isRefreshing = true
            storiesViewModel.refreshStories()
            binding?.swipeRefreshLayout?.isRefreshing = false
        }

        storiesViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error.code == 401) {
                binding?.tvError?.visibility = View.VISIBLE
                binding?.tvError?.text = requireContext().getString(R.string.error, "401 - ${error.message}")
            }
        }
    }

    private fun populateStories() {
        binding?.rvStories?.layoutManager = LinearLayoutManager(requireContext())
        val adapter = StoryAdapter()
        binding?.rvStories?.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest {
                binding?.loader?.isVisible = (it.refresh is LoadState.Loading) || (it.append is LoadState.Loading)

                val isLoading = it.refresh is LoadState.Loading ||
                        it.append is LoadState.Loading ||
                        it.prepend is LoadState.Loading

                if (!isLoading && storiesViewModel.hasScrolledToTop.value == false) {
                    binding?.rvStories?.scrollToPosition(0)
                    storiesViewModel.hasScrolledToTop.value = true
                }
            }
        }

        storiesViewModel.stories.observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)

            if (adapter.itemCount <= 0) {
                binding?.tvNoData?.visibility = View.VISIBLE
            } else {
                binding?.tvNoData?.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}