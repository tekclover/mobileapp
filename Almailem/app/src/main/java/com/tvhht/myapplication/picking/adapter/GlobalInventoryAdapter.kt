package com.tvhht.myapplication.picking.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tvhht.myapplication.databinding.AdapterGlobalInventoryBinding
import com.tvhht.myapplication.transfers.model.InventoryModel


class GlobalInventoryAdapter(val globalInventoryList: List<InventoryModel>) :
    RecyclerView.Adapter<GlobalInventoryAdapter.GlobalInventoryViewHolder>() {
    private var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GlobalInventoryViewHolder {
        context = parent.context
        val binding: AdapterGlobalInventoryBinding = AdapterGlobalInventoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GlobalInventoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GlobalInventoryViewHolder, position: Int) {
        holder.bindView()
    }

    override fun getItemCount(): Int {
        return globalInventoryList.size
    }

    inner class GlobalInventoryViewHolder(val binding: AdapterGlobalInventoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView() {
            val inventoryList = globalInventoryList[adapterPosition]
            with(binding) {
                textCompanyId.text = inventoryList.companyCodeId ?: ""
                textBranchId.text = inventoryList.plantId ?: ""
                textInvQty.text = (inventoryList.inventoryQuantity ?: 0).toString()
            }
        }
    }
}