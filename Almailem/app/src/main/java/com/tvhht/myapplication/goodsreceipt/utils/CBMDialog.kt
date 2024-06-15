package com.tvhht.myapplication.goodsreceipt.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.gson.Gson
import com.tvhht.myapplication.R
import com.tvhht.myapplication.databinding.DialogCbmBinding
import com.tvhht.myapplication.goodsreceipt.model.CBMResponse


class CBMDialog :
    DialogFragment {

    private lateinit var binding: DialogCbmBinding
    private var ctx: Context? = null
    private var cbmResponse: CBMResponse? = null
    private var callBack: CBMListener? = null

    constructor()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
        callBack = context as CBMListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ReasonDialogStyle)
        val cbm = arguments?.getString("CBM_RESPONSE")
        cbm?.let {
            cbmResponse = Gson().fromJson(it, CBMResponse::class.java)
        }
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
        binding = DialogCbmBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            edtTxtLength.setText((cbmResponse?.length ?: 0).toString())
            edtTxtWidth.setText((cbmResponse?.width ?: 0).toString())
            edtTxtHeight.setText((cbmResponse?.height ?: 0).toString())
            btnCancel.setOnClickListener {
                dismissAllowingStateLoss()
            }
            btnConfirm.setOnClickListener {
                dismissAllowingStateLoss()
                callBack?.onCBM(
                    edtTxtLength.text.toString(),
                    edtTxtWidth.text.toString(),
                    edtTxtHeight.text.toString()
                )
            }
        }
    }

    interface CBMListener {
        fun onCBM(length: String, width: String, height: String)
    }
}