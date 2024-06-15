package com.tvhht.myapplication.goodsreceipt.utils

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.gson.Gson
import com.tvhht.myapplication.R
import com.tvhht.myapplication.databinding.DialogDocumentSelectedBinding
import com.tvhht.myapplication.goodsreceipt.GoodsReceiptListActivity
import com.tvhht.myapplication.goodsreceipt.model.SelectedDocumentResponse


class BarcodeScanDialog(
    private val barcode: String,
    private val selectedDocumentResponse: SelectedDocumentResponse,
    private val grList: MutableList<SelectedDocumentResponse>
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
            textMessage.text = ctx?.resources?.getString(R.string.barcode_scanned)
            textTitle.text = barcode
            buttonYes.setOnClickListener {
                dismissAllowingStateLoss()
                val groupList =
                    grList.groupBy { it.itemCode }[selectedDocumentResponse.itemCode]?.filter { it.manufacturerName == selectedDocumentResponse.manufacturerName }
                        ?: emptyList()
                val intent = Intent(ctx, GoodsReceiptListActivity::class.java)
                intent.putExtra("SELECTED_DOCUMENT", Gson().toJson(selectedDocumentResponse))
                intent.putParcelableArrayListExtra("PART_NO_LIST", ArrayList(groupList))
                startActivity(intent)
            }
            buttonNo.setOnClickListener {
                dismissAllowingStateLoss()
            }
        }
    }
}