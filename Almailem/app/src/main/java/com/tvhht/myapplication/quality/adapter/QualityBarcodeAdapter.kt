package com.tvhht.myapplication.quality.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tvhht.myapplication.R
import com.tvhht.myapplication.databinding.AdapterBarcodeViewBinding
import com.tvhht.myapplication.quality.model.QualityBarcode
import java.util.Locale

class QualityBarcodeAdapter(
    private var barcodeList: List<QualityBarcode>,
    private val onSelectedBarcode: (barcode: String) -> Unit
) :
    RecyclerView.Adapter<QualityBarcodeAdapter.BarcodeViewModel>(), Filterable {
    private var ctx: Context? = null
    private var filterList: MutableList<QualityBarcode> = barcodeList.toMutableList()
    private lateinit var barcodeFilter: BarcodeFilter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarcodeViewModel {
        ctx = parent.context
        val binding: AdapterBarcodeViewBinding =
            AdapterBarcodeViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BarcodeViewModel(binding)
    }

    override fun getItemCount(): Int {
        return barcodeList.size
    }

    override fun onBindViewHolder(holder: BarcodeViewModel, position: Int) {
        holder.bindView()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(updateList: List<QualityBarcode>) {
        this.barcodeList = updateList
        notifyDataSetChanged()
    }

    inner class BarcodeViewModel(val binding: AdapterBarcodeViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bindView() {
            val barcodeDetails = barcodeList[adapterPosition]
            with(binding) {
                with(barcodeDetails) {
                    textBarcodeId.text = barcodeId ?: ""
                    imgCheck.setImageDrawable(ctx?.let {
                        ContextCompat.getDrawable(
                            it,
                            if (isChecked) R.drawable.ic_check_box_checked else R.drawable.ic_check_box_blank
                        )
                    })
                    binding.root.setOnClickListener {
                        if (this.isChecked) {
                            this.isChecked = false
                            onSelectedBarcode.invoke("")
                        } else {
                            for (value in barcodeList) {
                                value.isChecked = false
                            }
                            this.isChecked = true
                            this.barcodeId?.let { it1 -> onSelectedBarcode.invoke(it1) }
                        }
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun getFilter(): Filter {
        if (!this::barcodeFilter.isInitialized) {
            barcodeFilter = BarcodeFilter(this@QualityBarcodeAdapter)
        }
        return barcodeFilter
    }

    private inner class BarcodeFilter(private val adapter: QualityBarcodeAdapter) :
        Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filterResults = FilterResults()
            if (constraint.isEmpty()) {
                filterResults.values = filterList
                filterResults.count = filterList.size
            } else {
                val filteredList: MutableList<QualityBarcode> = mutableListOf()
                val filterPattern = constraint.toString().lowercase(Locale.getDefault())
                for (item in filterList) {
                    if (item.barcodeId?.lowercase(Locale.getDefault())
                            ?.contains(filterPattern) == true
                    ) {
                        filteredList.add(item)
                    }
                }
                filterResults.values = filteredList
                filterResults.count = filteredList.size
            }
            return filterResults
        }

        @Suppress("UNCHECKED_CAST")
        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            adapter.barcodeList = results.values as MutableList<QualityBarcode>
            adapter.notifyDataSetChanged()
        }
    }
}