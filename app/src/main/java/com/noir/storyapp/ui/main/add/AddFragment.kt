package com.noir.storyapp.ui.main.add

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import com.noir.storyapp.R
import com.noir.storyapp.databinding.FragmentAddBinding
import com.noir.storyapp.helper.Utils.getImageUri
import com.noir.storyapp.helper.Utils.reduceFileImage
import com.noir.storyapp.helper.Utils.uriToFile
import com.noir.storyapp.helper.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddFragment : Fragment() {
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding

    private var currentImageUri: Uri? = null

    private val addViewModel by viewModels<AddViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(requireContext(), REQUIRED_PERMISSION) == PackageManager.PERMISSION_GRANTED
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(requireContext(), "Permission request granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Permission request denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = Slide(Gravity.BOTTOM)
        exitTransition = Slide(Gravity.TOP)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding?.root
    }

    private val launchGallery = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private val launcherIntentCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess: Boolean ->
        if (isSuccess) {
            showImage()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding?.btnGallery?.setOnClickListener {
            launchGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding?.btnCamera?.setOnClickListener {
            currentImageUri = getImageUri(requireContext())
            launcherIntentCamera.launch(currentImageUri)
        }

        binding?.buttonAdd?.setOnClickListener {
            if (binding?.edtDescription?.text.toString().isEmpty()) {
                binding?.edtDescription?.error = "Silahkan masukkan deskripsi terlebih dahulu"
            } else if (currentImageUri == null) {
                Toast.makeText(requireContext(), "Masukkan gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
            else {
                uploadStory()
                addViewModel.uploadStory.observe(viewLifecycleOwner) { story ->
                    if (story.error) {
                        Toast.makeText(requireContext(), "Gagal Upload Story", Toast.LENGTH_SHORT).show()
                    } else {
                        val toStoriesFragment = AddFragmentDirections.actionNavigationAddToNavigationStories()
                        findNavController().navigate(toStoriesFragment)
                    }
                }
            }
        }

        addViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            loaderState(isLoading)
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding?.ivStory?.setImageURI(it)
        }
    }

    private fun uploadStory() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, requireContext()).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding?.edtDescription?.text.toString()

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            addViewModel.getSession().observe(viewLifecycleOwner) { user ->
                addViewModel.uploadStory(user.token, multipartBody, requestBody)
            }
        }
    }

    private fun loaderState(isLoading: Boolean) {
        if (isLoading) {
            binding?.buttonAdd?.text = ""
            binding?.loader?.visibility = View.VISIBLE
        } else {
            binding?.buttonAdd?.text = requireContext().resources.getString(R.string.upload)
            binding?.loader?.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}