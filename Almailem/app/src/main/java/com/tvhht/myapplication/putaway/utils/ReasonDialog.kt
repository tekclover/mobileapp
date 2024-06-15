package com.tvhht.myapplication.putaway.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.DialogFragment
import com.tvhht.myapplication.R
import com.tvhht.myapplication.databinding.DialogReasonBinding


class ReasonDialog : DialogFragment {

    private lateinit var binding: DialogReasonBinding
    private var ctx: Context? = null
    private var reasonListener: ReasonListener? = null
    private var selectedOption: String = ""
    private var isFrom: String = ""
    private var reasonList: MutableList<String>? = null

    constructor()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
        reasonListener = context as ReasonListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ReasonDialogStyle)
        isFrom = arguments?.getString("IS_REASON_FROM", "") ?: ""
    }

    override fun onStart() {
        super.onStart()
        val dialogWidth =
            resources.getDimensionPixelSize(R.dimen.dp_350)
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
        binding = DialogReasonBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (isFrom) {
            "GoodsReceipt" -> {
                binding.textTitle.text = this.resources.getString(R.string.reason_for_rejection)
                reasonList = this.resources.getStringArray(R.array.reason_for_rejection).asList()
                    .toMutableList()
            }

            "Inbound" -> {
                reasonList = this.resources.getStringArray(R.array.reason).asList().toMutableList()
            }

            "Outbound" -> {
                reasonList = this.resources.getStringArray(R.array.reason).asList().toMutableList()
                reasonList?.add("Show request")
            }
        }
        reasonList?.let {
            for (reason in it) {
                val radioButton = RadioButton(ctx)
                radioButton.text = reason
                radioButton.id = View.generateViewId() // Generate a unique ID for each RadioButton
                radioButton.setButtonDrawable(R.drawable.reason_radio_buttom_background)
                radioButton.background = null
                radioButton.setPadding(30, 14, 10, 14)
                binding.radioGroupReason.addView(radioButton)
            }
        }

        binding.radioGroupReason.setOnCheckedChangeListener { group, checkedId ->
            // Handle the RadioButton selection change here
            val selectedRadioButton: RadioButton = binding.radioGroupReason.findViewById(checkedId)
            selectedOption = selectedRadioButton.text.toString()
        }
        binding.btnCancel.setOnClickListener {
            dismissAllowingStateLoss()
        }
        binding.btnConfirm.setOnClickListener {
            dismissAllowingStateLoss()
            reasonListener?.selectedReason(selectedOption)
        }
    }

    interface ReasonListener {
        fun selectedReason(reason: String)
    }
}