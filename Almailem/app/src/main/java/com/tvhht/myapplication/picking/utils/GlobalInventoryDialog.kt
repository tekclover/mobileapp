package com.tvhht.myapplication.picking.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.tvhht.myapplication.R
import com.tvhht.myapplication.databinding.DialogGlobalInventoryBinding
import com.tvhht.myapplication.picking.adapter.GlobalInventoryAdapter
import com.tvhht.myapplication.transfers.model.InventoryModel


class GlobalInventoryDialog(private val inventoryList: List<InventoryModel>) :
    DialogFragment() {

    private lateinit var binding: DialogGlobalInventoryBinding
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
            dialogWidth
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogGlobalInventoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            val layoutManager = LinearLayoutManager(ctx)
            globalInventoryList.layoutManager = layoutManager
            globalInventoryList.addItemDecoration(
                DividerItemDecoration(
                    ctx,
                    layoutManager.orientation
                )
            )
            val adapter = GlobalInventoryAdapter(inventoryList)
            globalInventoryList.adapter = adapter

            btnCancel.setOnClickListener {
                dismissAllowingStateLoss()
            }
        }
    }
}