package com.tvhht.myapplication.quality.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tvhht.myapplication.databinding.AdapterQualityHeNoBinding
import com.tvhht.myapplication.picking.model.PickingListResponse
import com.tvhht.myapplication.quality.model.QualityListResponse

class QualityHeNumberListAdapter(
    private var heNumberList: List<QualityListResponse>,
    private val onHeNumberSelection: (heNumber: String) -> Unit
) :
    RecyclerView.Adapter<QualityHeNumberListAdapter.QualityHeNumberViewModel>() {
    private var ctx: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QualityHeNumberViewModel {
        ctx = parent.context
        val binding: AdapterQualityHeNoBinding =
            AdapterQualityHeNoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QualityHeNumberViewModel(binding)
    }

    override fun getItemCount(): Int {
        return heNumberList.size
    }

    override fun onBindViewHolder(holder: QualityHeNumberViewModel, position: Int) {
        holder.bindView()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(updateList: List<QualityListResponse>) {
        heNumberList = updateList
        notifyDataSetChanged()
    }

    inner class QualityHeNumberViewModel(val binding: AdapterQualityHeNoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView() {
            val quality = heNumberList[adapterPosition]
            with(binding) {
                with(quality) {
                    txtCell1.text = actualHeNo ?: ""
                    txtCell2.setOnClickListener {
                        txtCell2.isChecked = false
                        actualHeNo?.let { it1 -> onHeNumberSelection.invoke(it1) }
                    }
                }
            }
        }
    }
}