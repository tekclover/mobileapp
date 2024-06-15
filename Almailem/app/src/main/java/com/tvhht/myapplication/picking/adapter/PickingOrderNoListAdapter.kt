package com.tvhht.myapplication.picking.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tvhht.myapplication.databinding.AdapterPickingOrderNoBinding
import com.tvhht.myapplication.picking.model.PickingListResponse
import com.tvhht.myapplication.utils.ApiConstant

class PickingOrderNoListAdapter(
    private var pickingOrderNoList: List<PickingListResponse>,
    private val onPickingSelection: (picking:PickingListResponse) -> Unit
) :
    RecyclerView.Adapter<PickingOrderNoListAdapter.PickingOrderNoViewModel>() {
    private var ctx: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickingOrderNoViewModel {
        ctx = parent.context
        val binding: AdapterPickingOrderNoBinding =
            AdapterPickingOrderNoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PickingOrderNoViewModel(binding)
    }

    override fun getItemCount(): Int {
        return pickingOrderNoList.size
    }

    override fun onBindViewHolder(holder: PickingOrderNoViewModel, position: Int) {
        holder.bindView()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(updateList: List<PickingListResponse>) {
        pickingOrderNoList = updateList
        notifyDataSetChanged()
    }

    inner class PickingOrderNoViewModel(val binding: AdapterPickingOrderNoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView() {
            val pickingOrderNo = pickingOrderNoList[adapterPosition]
            with(binding) {
                with(pickingOrderNo) {
                    txtCell1.text = if (referenceDocumentType == ApiConstant.PICK_LIST) salesOrderNumber else refDocNumber
                    txtCell2.text = manufacturerName ?: ""
                    txtCell3.text = itemCode ?: ""
                    txtCell4.text = proposedStorageBin ?: ""

                    root.setOnClickListener {
                       /* WMSSharedPref.getAppPrefs(
                            WMSApplication.getInstance().context
                        ).savePickingInfo(pickingOrderNo)
                        ctx?.let {
                            val myIntent = Intent(it, PickingDetailsActivity::class.java)
                            myIntent.putExtra("DEFAULT_BIN_NO", this.proposedStorageBin)
                            myIntent.putExtra("DEFAULT_PALLET_NO", this.proposedPackBarCode)
                            it.startActivity(myIntent)
                        }*/
                        onPickingSelection.invoke(pickingOrderNo)
                    }
                }
            }
        }
    }
}