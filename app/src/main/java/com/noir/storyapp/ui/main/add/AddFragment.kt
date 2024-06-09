package com.noir.storyapp.ui.main.add

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.noir.storyapp.R
import com.noir.storyapp.databinding.FragmentAddBinding
import com.noir.storyapp.helper.Utils.getImageUri
import com.noir.storyapp.helper.Utils.reduceFileImage
import com.noir.storyapp.helper.Utils.uriToFile
import com.noir.storyapp.helper.ViewModelFactory
import com.noir.storyapp.ui.main.stories.StoriesViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddFragment : Fragment() {
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding

    private var currentImageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var myLocation: Location
    private var selectedLocationState = false
    private var sendLocationIsChecked = false
    private var doNotSendLocationIsChecked = false

    private val addViewModel by viewModels<AddViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private val storiesViewModel by viewModels<StoriesViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            permissions[Manifest.permission.CAMERA] ?: false -> {
                Toast.makeText(requireContext(), "Permission request granted", Toast.LENGTH_SHORT).show()
            }
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                // Precise location access granted
                getMyLastLocation()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                // Only approximate location access granted
                getMyLastLocation()
            }
            else -> {
                Toast.makeText(requireContext(), "Permission request denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = Slide(Gravity.BOTTOM)
        exitTransition = Slide(Gravity.TOP)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
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

        getMyLastLocation()

        binding?.btnGallery?.setOnClickListener {
            launchGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding?.btnCamera?.setOnClickListener {
            currentImageUri = getImageUri(requireContext())
            launcherIntentCamera.launch(currentImageUri)
        }

        binding?.buttonAdd?.setOnClickListener {
            if (currentImageUri == null) {
                Toast.makeText(requireContext(), "Masukkan gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
            } else if (binding?.edtDescription?.text.toString().isEmpty()) {
                binding?.edtDescription?.error = "Silahkan masukkan deskripsi terlebih dahulu"
            } else if (!binding?.radioSendLoc?.isChecked!! && !binding?.radioDontSendLoc?.isChecked!!) {
                Toast.makeText(requireContext(), "Silahkan pilih apakah lokasi anda mau dikirim juga?", Toast.LENGTH_SHORT).show()
            }
            else {
                getMyLastLocation()
                uploadStory()
                Log.d(TAG, selectedLocationState.toString())
            }
        }

        addViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            loaderState(isLoading)
        }
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.CAMERA) && checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    myLocation = location
                    Log.d("My Location: ", "${myLocation.latitude}, ${myLocation.longitude}")
                }
            }
        } else {
            requestPermissionLauncher.launch(arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
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

            val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            val latRequestBody = myLocation.latitude.toString().toRequestBody("text/plain".toMediaType())
            val lonRequestBody = myLocation.longitude.toString().toRequestBody("text/plain".toMediaType())

            binding?.radioSendLoc?.setOnCheckedChangeListener { _, isChecked ->
                sendLocationIsChecked = isChecked
            }

            binding?.radioDontSendLoc?.setOnCheckedChangeListener { _, isChecked ->
                doNotSendLocationIsChecked = isChecked
            }

            if (sendLocationIsChecked) {
                selectedLocationState = true
            } else if (doNotSendLocationIsChecked) {
                selectedLocationState = false
            }

            if (selectedLocationState) {
                addViewModel.getSession().observe(viewLifecycleOwner) { user ->
                    addViewModel.uploadStory(user.token, multipartBody, descriptionRequestBody, latRequestBody, lonRequestBody)
                }
            } else {
                addViewModel.getSession().observe(viewLifecycleOwner) { user ->
                    addViewModel.uploadStory(user.token, multipartBody, descriptionRequestBody)
                }
            }

            addViewModel.uploadStory.observe(viewLifecycleOwner) { story ->
                if (!story.error && story.message == "Story created successfully") {
                    val toStoriesFragment = AddFragmentDirections.actionNavigationAddToNavigationStories()
                    findNavController().navigate(toStoriesFragment)
                    storiesViewModel.hasScrolledToTop.value = false
                } else {
                    Toast.makeText(requireContext(), "Gagal Upload Story", Toast.LENGTH_SHORT).show()
                }
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
        const val TAG = "AddFragment"
    }
}