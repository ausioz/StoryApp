package com.example.storyapp.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toIcon
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.storyapp.R
import com.example.storyapp.data.response.ListStoryItem
import com.example.storyapp.data.response.StoryResponse
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.databinding.CustomInfoContentLayoutBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker


//class MarkerInfoWindowAdapter(private val photoUrl: String?) : GoogleMap.InfoWindowAdapter {
//
//    private val contents = layoutInflater.inflate(R.layout.custom_info_content_layout, null)
//    override fun getInfoWindow(marker: Marker): View? {
//        return null
//    }
//
//    override fun getInfoContents(marker: Marker): View? {
//        render(marker, contents, photoUrl)
//        return contents
//    }
//
//    private fun render(marker: Marker, view: View, photoUrl: String?) {
//        Glide.with(this)
//            .load(photoUrl)
//            .into(object : CustomTarget<Drawable>() {
//                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
//                    // load your resource into mapView
//                    view.findViewById<ImageView>(R.id.iv_photo).setImageDrawable(resource)
//                }
//                override fun onLoadCleared(placeholder: Drawable?) {}
//            })
//        val user = view.findViewById<TextView>(R.id.tv_user)
//        val desc = view.findViewById<TextView>(R.id.tv_desc)
//
//        user.text = marker.title
//        desc.text = marker.snippet
//    }
//
//
//}
