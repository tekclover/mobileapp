package com.clara.clientportal.ui.documentupload

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clara.clientportal.R
import com.clara.clientportal.databinding.DialogUploadBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FragmentUploadDocument : BottomSheetDialogFragment {

    private lateinit var binding: DialogUploadBinding
    private var uploadDocumentListener: UploadDocumentListener? = null

    constructor()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        uploadDocumentListener = context as UploadDocumentListener
    }

    override fun onStart() {
        super.onStart()
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogUploadBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListener()
    }

    private fun setListener() {
        with(binding) {
            imgCamera.setOnClickListener {
                dismissAllowingStateLoss()
                uploadDocumentListener?.onCamera()
            }
            imgGallery.setOnClickListener {
                dismissAllowingStateLoss()
                uploadDocumentListener?.onGallery()
            }
            imgFiles.setOnClickListener {
                dismissAllowingStateLoss()
                uploadDocumentListener?.onFiles()
            }
            imgClose.setOnClickListener {
                dismissAllowingStateLoss()
            }
        }
    }

    interface UploadDocumentListener {
        fun onCamera()
        fun onGallery()
        fun onFiles()
    }
}