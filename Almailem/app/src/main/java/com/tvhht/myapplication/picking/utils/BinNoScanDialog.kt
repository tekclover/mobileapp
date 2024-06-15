package com.tvhht.myapplication.picking.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.tvhht.myapplication.R
import com.tvhht.myapplication.databinding.DialogDocumentSelectedBinding
import com.tvhht.myapplication.picking.model.PickingListResponse


class BinNoScanDialog(
    private val binNo: String,
    private val pickingCombineResponse: PickingListResponse,
    private val callBack: BinNumberScanDialogListener
) :
    DialogFragment() {

    private lateinit var binding: DialogDocumentSelectedBinding
    private var ctx: Context? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ReasonDialogStyle)
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
        binding = DialogDocumentSelectedBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            textMessage.text = ctx?.resources?.getString(R.string.bin_no_scanned)
            textTitle.text = binNo
            buttonYes.setOnClickListener {
                dismissAllowingStateLoss()
                callBack.onPickingSelected(pickingCombineResponse)
                /*
                                ctx?.let {
                                    WMSSharedPref.getAppPrefs(
                                        WMSApplication.getInstance().context
                                    ).savePickingInfo(pickingCombineResponse)
                                    val myIntent = Intent(it, PickingDetailsActivity::class.java)
                                    myIntent.putExtra("DEFAULT_BIN_NO", pickingCombineResponse.proposedStorageBin)
                                    myIntent.putExtra(
                                        "DEFAULT_PALLET_NO",
                                        pickingCombineResponse.proposedPackBarCode
                                    )
                                    it.startActivity(myIntent)
                                }
                */
            }
            buttonNo.setOnClickListener {
                dismissAllowingStateLoss()
            }
        }
    }

    interface BinNumberScanDialogListener {
        fun onPickingSelected(picking: PickingListResponse)
    }
}