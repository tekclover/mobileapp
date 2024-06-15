package com.clara.clientportal.ui.document

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.clara.clientportal.BaseFragment
import com.clara.clientportal.databinding.FragmentDocumentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DocumentFragment : BaseFragment() {
    private lateinit var binding: FragmentDocumentBinding
    private val viewModel: DocumentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDocumentBinding.inflate(inflater)
        return binding.root
    }
}