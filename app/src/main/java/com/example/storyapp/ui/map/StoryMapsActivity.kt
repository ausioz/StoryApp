package com.example.storyapp.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.storyapp.R
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.databinding.ActivityStoryMapsBinding
import com.example.storyapp.databinding.CustomInfoContentLayoutBinding
import com.example.storyapp.ui.customview.LoadingDialogFragment
import com.example.storyapp.ui.main.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class StoryMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityStoryMapsBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this, application)
    }
    private val loadingDialog = LoadingDialogFragment()
    private val boundsBuilder = LatLngBounds.Builder()

    //initiate those two variables
    private val images: HashMap<Marker, Bitmap> = HashMap()
    private val targets: HashMap<Marker, CustomTarget<Bitmap>> = HashMap()

    private var photoUrl = mutableListOf<String?>()
    private var markerId = mutableListOf<String?>()

    inner class MarkerInfoWindowAdapter(
        private val context: Context
    ) : GoogleMap.InfoWindowAdapter {

        private val layoutInflater = (this@StoryMapsActivity).layoutInflater
        private val contents = CustomInfoContentLayoutBinding.inflate(layoutInflater)
        override fun getInfoWindow(marker: Marker): View? {
            return null
        }

        override fun getInfoContents(marker: Marker): View {
            render(marker,contents)
            return contents.root
        }

        private fun render(marker: Marker, binding: CustomInfoContentLayoutBinding) {
            var photo = ""
                photo = when (marker.id) {
                    markerId[0] -> photoUrl[0].toString()
                    markerId[1] -> photoUrl[1].toString()
                    markerId[2] -> photoUrl[2].toString()
                    markerId[3] -> photoUrl[3].toString()
                    markerId[4] -> photoUrl[4].toString()
                    markerId[5] -> photoUrl[5].toString()
                    markerId[6] -> photoUrl[6].toString()
                    markerId[7] -> photoUrl[7].toString()
                    markerId[8] -> photoUrl[8].toString()
                    markerId[9] -> photoUrl[9].toString()
                    else -> {
                        ""
                    }
                }
            Glide.with(context).load(photo).into(binding.ivPhoto)
            Log.d("imgurl", "render: $photo")
            binding.tvUser.text = marker.title
            binding.tvDesc.text = marker.snippet
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoryMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.errorMsg.observe(this) {
            showError(it)
        }

        viewModel.getStoriesWithLocation()

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        // Add a marker

        viewModel.listStory.observe(this) {

            it.listStory?.forEach { story ->
                val latLng =
                    if (story.lat != null && story.lon != null) LatLng(story.lat, story.lon)
                    else LatLng(0.0, 0.0)
                val marker = mMap.addMarker(
                    MarkerOptions().position(latLng).title(story.name).snippet(story.description)
                )
                mMap.addMarker(
                    MarkerOptions().position(latLng).title(story.name).snippet(story.description)
                )
                photoUrl.add(story.photoUrl)
                markerId.add(marker?.id)


                boundsBuilder.include(latLng)
            }
            mMap.setInfoWindowAdapter(MarkerInfoWindowAdapter(this))
            Log.d("lmao", "onMapReady: ${photoUrl.toList()}")
            Log.d("lmao", "onMapReady: ${markerId.toList()}")
            val bounds: LatLngBounds = boundsBuilder.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    300
                )
            )
        }



        mMap.setOnPoiClickListener {
            val poi = mMap.addMarker(MarkerOptions().position(it.latLng).title(it.name))
            poi?.showInfoWindow()
        }
    }


    private fun showError(errorMsg: String?) {
        Toast.makeText(this, "Error! \n$errorMsg", Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        loadingDialog.isCancelable = false
        if (isLoading) {
            loadingDialog.show(supportFragmentManager, "loadingDialog")
        } else {
            if (loadingDialog.isVisible) loadingDialog.dismiss()
        }
    }


}