package com.example.storyapp.ui.customview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.storyapp.databinding.DialogLoadingLayoutBinding

class LoadingDialogFragment : DialogFragment() {
    private lateinit var binding: DialogLoadingLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DialogLoadingLayoutBinding.inflate(layoutInflater, container, false)
        return binding.root
    }



}