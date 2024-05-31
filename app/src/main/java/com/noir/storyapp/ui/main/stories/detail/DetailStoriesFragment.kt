package com.noir.storyapp.ui.main.stories.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.noir.storyapp.databinding.FragmentDetailStoriesBinding

class DetailStoriesFragment : Fragment() {
    private var _binding: FragmentDetailStoriesBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailStoriesBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()

        val data = DetailStoriesFragmentArgs.fromBundle(arguments as Bundle)
        Glide.with(requireContext())
            .load(data.image)
            .into(binding!!.ivDetailPhoto)
        binding?.tvDetailName?.text = data.name
        binding?.tvDetailDescription?.text = data.description

        (view.parent as? ViewGroup)?.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}