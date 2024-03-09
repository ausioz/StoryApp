package com.example.storyapp.ui.map

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
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
import com.google.android.gms.maps.model.MapStyleOptions
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

    private var photoUrl = mutableListOf<String?>()
    private var markerId = mutableListOf<String?>()

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
        setMapStyle()


        viewModel.listStory.observe(this) {

            it.listStory.forEach { story ->
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

    }
    inner class MarkerInfoWindowAdapter(private val context: Context) :
        GoogleMap.InfoWindowAdapter {

        private var binding = CustomInfoContentLayoutBinding.inflate(LayoutInflater.from(context))
        private val images: HashMap<Marker, Bitmap> = HashMap()
        private val targets: HashMap<Marker, CustomTarget<Bitmap>> = HashMap()

        private fun bind(marker: Marker) {
            val image = images[marker]
            var photo = ""
            markerId.forEachIndexed { index, value ->
                when (marker.id) {
                    value -> photo = photoUrl[index].toString()
                }
            }

            with(binding) {
                tvUser.text = marker.title
                tvDesc.text = marker.snippet
                if (image == null) {
                    Glide.with(context).asBitmap().load(photo).dontAnimate().into(getTarget(marker))

                } else {
                    ivPhoto.setImageBitmap(image)
                }
            }


        }

        override fun getInfoContents(marker: Marker): View {
            bind(marker)
            return binding.root
        }

        override fun getInfoWindow(marker: Marker): View {
            bind(marker)
            return binding.root
        }

        inner class InfoTarget(private var marker: Marker) : CustomTarget<Bitmap>() {

            override fun onLoadCleared(placeholder: Drawable?) {
                images.remove(marker)
            }

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                images[marker] = resource
                marker.showInfoWindow()
            }
        }

        private fun getTarget(marker: Marker): CustomTarget<Bitmap> {
            var target = targets[marker]
            if (target == null) {
                target = InfoTarget(marker)
                targets[marker] = target
            }
            return target
        }
    }
    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e("StoryMapsActivity", "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e("StoryMapsActivity", "Can't find style. Error: ", exception)
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