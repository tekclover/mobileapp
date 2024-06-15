package com.clara.clientportal.ui.matter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.clara.clientportal.R
import com.clara.clientportal.databinding.FragmentViewMatterBinding
import com.clara.clientportal.utils.Constants
import com.clara.clientportal.utils.PreferenceHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FragmentMatterDetails : DialogFragment {

    private lateinit var binding: FragmentViewMatterBinding
    private val viewModel: MatterPopupViewModel by viewModels()

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    constructor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Dialog)
    }

    override fun onStart() {
        super.onStart()
        val dialogWidth =
            resources.getDimensionPixelSize(if (preferenceHelper.isTablet()) R.dimen.dp_400 else R.dimen.dp_300)
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
        binding = FragmentViewMatterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.matterNo = arguments?.getString(Constants.MATTER_NO, "") ?: ""
        binding.textMatterNo.text = String.format(
            requireActivity().resources.getString(R.string.matter),
            viewModel.matterNo
        )
        binding.imgClose.setOnClickListener {
            dismissAllowingStateLoss()
        }

        viewModel.matterPopupMutableLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                with(binding) {
                    with(it) {
                        textOpenOn.text = this.caseOpenedDate ?: Constants.NA
                        textFiledOn.text = this.caseFiledDate ?: Constants.NA
                        textReceiptOn.text = this.receiptDate ?: Constants.NA
                        textRfeOn.text = this.courtDate ?: Constants.NA
                        textApprovedOn.text = this.approvalDate ?: Constants.NA
                        textClosedOn.text = this.caseClosedDate ?: Constants.NA
                    }
                }
            }
        }
    }
}