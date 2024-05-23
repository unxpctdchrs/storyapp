package com.noir.storyapp.ui.main.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.noir.storyapp.data.pref.UserModel
import com.noir.storyapp.databinding.FragmentSettingsBinding
import com.noir.storyapp.helper.ViewModelFactory

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding

    private val settingsViewModel by viewModels<SettingsViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnLogout?.setOnClickListener {
            settingsViewModel.logout()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}