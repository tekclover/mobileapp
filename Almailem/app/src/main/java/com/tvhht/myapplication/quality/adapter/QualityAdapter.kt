package com.tvhht.myapplication.quality.adapter


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.tvhht.myapplication.R
import com.tvhht.myapplication.cases.ASNNumberListActivity
import com.tvhht.myapplication.quality.model.QualityListResponse
import com.tvhht.myapplication.quality.utils.HECustomDialog
import com.tvhht.myapplication.quality.utils.OrderNumberCustomDialog
import com.tvhht.myapplication.utils.ApiConstant
import com.tvhht.myapplication.utils.DateUtil
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import kotlinx.android.synthetic.main.list_quality_details_cell.view.*
import java.util.Locale

class QualityAdapter(val ctx: Context, var data: List<QualityListResponse>) :
    RecyclerView.Adapter<QualityAdapter.ViewHolder>(), Filterable {

    private val filterList: MutableList<QualityListResponse> = data.toMutableList()
    private lateinit var orderFilter: OrderFilter

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QualityAdapter.ViewHolder {
        val v =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_quality_details_cell, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: QualityAdapter.ViewHolder, position: Int) {
        holder.bindItems(data[position])
        val iValue = position + 1;
        // holder.sno.text = iValue.toString()

        holder.isselected.setOnClickListener {

            holder.isselected.isChecked = false
            WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
            ).saveStringValue(PrefConstant.QUALITY_ORDER_TYPE, data[position].referenceDocumentType)
            val myIntent =
                Intent(ctx, OrderNumberCustomDialog::class.java)
            myIntent.putExtra("ORDER_NO", if (data[position].referenceDocumentType == ApiConstant.PICK_LIST) data[position].salesOrderNumber else data[position].refDocNumber)
            myIntent.putExtra("TITLE", "Verify Order Number")
            ctx.startActivity(myIntent)
        }

    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return data.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val branch = itemView.txt_cell_1
        val orderNo = itemView.txt_cell_2
        val orderType = itemView.txt_cell_3
        val isselected = itemView.txt_cell_4

        fun bindItems(model: QualityListResponse) {
            // orderNo.text=model.refDocNumber
            // date.text = DateUtil.getDateYYYYMMDD(model.qualityCreatedOn)
            branch.text = model.plantId ?: ""
            orderNo.text = if (model.referenceDocumentType == ApiConstant.PICK_LIST) model.salesOrderNumber else model.refDocNumber
            orderType.text = model.referenceDocumentType ?: ""
            isselected.isChecked = model.isSelected
        }
    }

    override fun getFilter(): Filter {
        if (!this::orderFilter.isInitialized) {
            orderFilter = OrderFilter(this@QualityAdapter)
        }
        return orderFilter
    }

    private inner class OrderFilter(private val adapter: QualityAdapter) :
        Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filterResults = FilterResults()
            if (constraint.isEmpty()) {
                filterResults.values = filterList
                filterResults.count = filterList.size
            } else {
                val filteredList: MutableList<QualityListResponse> = mutableListOf()
                val filterPattern = constraint.toString().lowercase(Locale.getDefault())
                for (item in filterList) {
                    if (item.refDocNumber?.lowercase(Locale.getDefault())
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
            adapter.data = results.values as MutableList<QualityListResponse>
            adapter.notifyDataSetChanged()
        }
    }
}