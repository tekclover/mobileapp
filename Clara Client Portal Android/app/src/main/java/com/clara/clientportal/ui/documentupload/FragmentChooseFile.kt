package com.clara.clientportal.ui.documentupload

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.clara.clientportal.R
import com.clara.clientportal.databinding.DialogUploadDocumentBinding
import com.clara.clientportal.network.APIConstant
import com.clara.clientportal.utils.CommonUtils
import com.clara.clientportal.utils.Constants
import com.clara.clientportal.utils.PreferenceHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FragmentChooseFile : DialogFragment {
    @Inject
    lateinit var commonUtils: CommonUtils

    @Inject
    lateinit var preferenceHelper: PreferenceHelper
    private lateinit var binding: DialogUploadDocumentBinding
    private val viewModel: DocumentUploadViewModel by activityViewModels()
    var callBack: ChooseFileListener? = null

    constructor()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callBack = context as ChooseFileListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Dialog)
    }

    override fun onStart() {
        super.onStart()
        val dialogWidth =
            resources.getDimensionPixelSize(if (preferenceHelper.isTablet()) R.dimen.dp_500 else R.dimen.dp_300)
        dialog?.window?.setLayout(
            dialogWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogUploadDocumentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.matterNoList?.let {
            val arrayAdapter =
                ArrayAdapter(requireContext(), R.layout.drop_down_item, it)
            binding.spinner.adapter = arrayAdapter
            binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    if (selectedItem != Constants.SELECT) {
                        viewModel.matterId = selectedItem
                        viewModel.getAuthToken(APIConstant.DOCUMENT_UPLOAD_MATTER_ID)
                    } else {
                        viewModel.matterId = ""
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
        }
        setListener()
        viewModel.uploadList.observe(viewLifecycleOwner) {
            binding.textFileName.text = it.name ?: ""
        }
    }

    private fun setListener() {
        binding.btnChooseFile.setOnClickListener {
            if (viewModel.matterId.isNullOrEmpty().not()) {
                callBack?.showUploadFragment()
            } else {
                commonUtils.showToastMessage(
                    requireContext(),
                    this.resources.getString(R.string.please_choose_matter_id)
                )
            }
        }
        binding.btnCancel.setOnClickListener {
            dismissAllowingStateLoss()
        }
        binding.imgClose.setOnClickListener {
            dismissAllowingStateLoss()
        }
        binding.btnSave.setOnClickListener {
            when {
                viewModel.matterId.isNullOrEmpty() && binding.textFileName.text.toString()
                    .isEmpty() -> {
                    commonUtils.showToastMessage(
                        requireContext(),
                        this.resources.getString(R.string.matter_id_and_file_empty_message)
                    )
                }

                viewModel.matterId.isNullOrEmpty() -> {
                    commonUtils.showToastMessage(
                        requireContext(),
                        this.resources.getString(R.string.please_choose_matter_id)
                    )
                }

                binding.textFileName.text.toString().isEmpty() -> {
                    commonUtils.showToastMessage(
                        requireContext(),
                        this.resources.getString(R.string.please_select_file)
                    )
                }

                else -> {
                    dismissAllowingStateLoss()
                    viewModel.getAuthToken(APIConstant.UPLOAD)
                }
            }
        }
    }

    interface ChooseFileListener {
        fun showUploadFragment()
    }
}