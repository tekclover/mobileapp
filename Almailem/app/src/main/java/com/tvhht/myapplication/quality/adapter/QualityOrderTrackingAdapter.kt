package com.tvhht.myapplication.quality.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tvhht.myapplication.R
import com.tvhht.myapplication.databinding.AdapterOrderTrackingReportBinding
import com.tvhht.myapplication.quality.model.OutboundLineResponse

class QualityOrderTrackingAdapter(
    private var outboundLineList: List<OutboundLineResponse>
) :
    RecyclerView.Adapter<QualityOrderTrackingAdapter.OrderTRackingViewModel>() {
    private var ctx: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderTRackingViewModel {
        ctx = parent.context
        val binding: AdapterOrderTrackingReportBinding =
            AdapterOrderTrackingReportBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return OrderTRackingViewModel(binding)
    }

    override fun getItemCount(): Int {
        return outboundLineList.size
    }

    override fun onBindViewHolder(holder: OrderTRackingViewModel, position: Int) {
        holder.bindView()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(updateList: List<OutboundLineResponse>) {
        outboundLineList = updateList
        notifyDataSetChanged()
    }

    inner class OrderTRackingViewModel(val binding: AdapterOrderTrackingReportBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView() {
            val outboundLine = outboundLineList[adapterPosition]
            with(binding) {
                with(outboundLine) {
                    txtCell1.text = refDocNumber ?: ""
                    txtCell2.text = itemCode ?: ""
                    txtCell3.text = handlingEquipment ?: ""
                    val qty = referenceField9?.toDouble() ?: 0
                    txtCell4.text = qty.toInt().toString()
                    txtCell5.text = assignedPickerId ?: ""
                    ctx?.let {
                        when (statusId) {
                            48 -> {
                                txtCell2.setTextColor(
                                    ContextCompat.getColor(
                                        it,
                                        R.color.dark_blue
                                    )
                                )
                            }

                            50 -> {
                                txtCell2.setTextColor(ContextCompat.getColor(it, R.color.dark_orange))
                            }

                            57 -> {
                                txtCell2.setTextColor(
                                    ContextCompat.getColor(
                                        it,
                                        R.color.dark_green
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}