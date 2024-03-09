package com.example.storyapp.ui.story.upload

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.storyapp.R
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.databinding.ActivityUploadStoryBinding
import com.example.storyapp.ui.customview.LoadingDialogFragment
import com.example.storyapp.ui.main.MainActivity
import com.example.storyapp.ui.story.reduceFileImage
import com.example.storyapp.ui.story.upload.CameraActivity.Companion.CAMERAX_RESULT
import com.example.storyapp.ui.story.upload.CameraActivity.Companion.EXTRA_CAMERAX_IMAGE
import com.example.storyapp.ui.story.uriToFile
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class UploadStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadStoryBinding
    private val viewModel by viewModels<UploadStoryViewModel> {
        ViewModelFactory.getInstance(this, application)
    }
    private var currentImageUri: Uri? = null
    private val loadingDialog = LoadingDialogFragment()


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var isTracking = false
    private var lastLatLng: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.errorMsg.observe(this) {
            showError(it)
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        binding.galleryButton.setOnClickListener {
            launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.cameraButton.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            launcherIntentCameraX.launch(intent)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getMyLastLocation()
        createLocationRequest()
        createLocationCallback()
        binding.locationSwitch.setOnCheckedChangeListener { _, isChecked ->
            isTracking = isChecked
            if (isTracking) {
                startLocationUpdate()
                binding.ivLocation.setColorFilter(R.color.pruple)
            } else {
                stopLocationUpdate()
                binding.ivLocation.setColorFilter(R.color.gray)
                binding.tvLatitude.text = getString(R.string.latitude)
                binding.tvLongitude.text = getString(R.string.longitude)
            }
        }
        Log.d(TAG, "onCreate: $lastLatLng")
        binding.uploadButton.setOnClickListener { uploadImage() }

    }

    private fun stopLocationUpdate() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    private fun startLocationUpdate() {
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )
        } catch (exception: SecurityException) {
            Log.e(TAG, "Error : " + exception.message)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isTracking) {
            startLocationUpdate()
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdate()
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                for (location in p0.locations) {
                    Log.d(TAG, "onLocationResult: ${location.latitude} ${location.longitude}")
                    lastLatLng = location
                    binding.tvLatitude.text =
                        getString(R.string.latitude_value, location.latitude.toString())
                    binding.tvLongitude.text =
                        getString(R.string.longitude_value, location.longitude.toString())
                }
            }
        }
    }


    private val resolutionLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        when (result.resultCode) {
            RESULT_OK -> Log.i(TAG, "onActivityResult: All location settings are satisfied.")

            RESULT_CANCELED -> Toast.makeText(
                this, "Anda harus mengaktifkan GPS untuk menggunakan fitur ini!", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun createLocationRequest() {
        locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, TimeUnit.SECONDS.toMillis(1))
                .setIntervalMillis(TimeUnit.SECONDS.toMillis(1))
                .setMaxUpdateDelayMillis(TimeUnit.SECONDS.toMillis(1))
                .setWaitForAccurateLocation(true).build()

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)
        client.checkLocationSettings(builder.build()).addOnSuccessListener {
            getMyLastLocation()
        }.addOnFailureListener {
            if (it is ResolvableApiException) {
                try {
                    resolutionLauncher.launch(
                        IntentSenderRequest.Builder(it.resolution).build()
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Toast.makeText(this, sendEx.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                // Precise location access granted.
                getMyLastLocation()
            }

            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                // Only approximate location access granted.
                getMyLastLocation()
            }

            else -> {
                // No location access granted.
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    startLocation(it)
                } else {
                    Toast.makeText(this, "Location not Found. Try Again", Toast.LENGTH_SHORT).show()
                }
            }

        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun startLocation(location: Location): LatLng {
        return LatLng(location.latitude, location.longitude)
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private val launcherIntentCameraX =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == CAMERAX_RESULT) {
                currentImageUri = it.data?.getStringExtra(EXTRA_CAMERAX_IMAGE)?.toUri()
                showImage()
            }
        }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.descriptionET.text.toString()
            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo", imageFile.name, requestImageFile
            )

            val lat = if (binding.tvLatitude.text.toString() == getString(R.string.latitude)) {
                null
            } else binding.tvLatitude.text.toString().toFloat()
            val long = if (binding.tvLongitude.text.toString() == getString(R.string.longitude)) {
                null
            } else binding.tvLongitude.text.toString().toFloat()

            viewModel.getSession().observe(this) {
                viewModel.uploadStory(multipartBody, requestBody, lat, long)
            }
            viewModel.uploadResponse.observe(this) { response ->
                if (!response.error) {
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }

        } ?: showError(getString((R.string.masukan_berkas_gambar)))
    }

    private fun showLoading(isLoading: Boolean) {
        loadingDialog.isCancelable = false
        if (isLoading) {
            loadingDialog.show(supportFragmentManager, "loadingDialog")
        } else {
            if (loadingDialog.isVisible) loadingDialog.dismiss()
        }
    }

    private fun showError(errorMsg: String?) {
        Toast.makeText(this, "Error! \n$errorMsg", Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG = "UploadStoryActivity"
    }
}