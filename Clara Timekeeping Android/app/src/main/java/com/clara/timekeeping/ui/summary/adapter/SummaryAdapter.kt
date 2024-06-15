package com.clara.timekeeping.ui.summary.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.clara.timekeeping.R
import com.clara.timekeeping.databinding.AdapterSummaryBinding
import com.clara.timekeeping.model.TimeTicketSummaryResponse
import com.clara.timekeeping.utils.CommonUtils
import com.clara.timekeeping.utils.Constants
import com.clara.timekeeping.utils.toDollar
import java.util.Locale

class SummaryAdapter(
    private var summaryList: List<TimeTicketSummaryResponse>,
    private val commonUtils: CommonUtils,
    private val onViewClick: (timeTicketSummary: TimeTicketSummaryResponse, actionId: Int) -> Unit
) :
    RecyclerView.Adapter<SummaryAdapter.SummaryViewHolder>(), Filterable {
    private var context: Context? = null
    private val filterList: MutableList<TimeTicketSummaryResponse> = summaryList.toMutableList()
    private lateinit var summaryFilter: SummaryFilter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummaryViewHolder {
        context = parent.context
        val binding =
            AdapterSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SummaryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return summaryList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(updateList: List<TimeTicketSummaryResponse>) {
        this.summaryList = updateList
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        if (this::summaryFilter.isInitialized.not()) {
            summaryFilter = SummaryFilter(this@SummaryAdapter)
        }
        return summaryFilter
    }

    override fun onBindViewHolder(holder: SummaryViewHolder, position: Int) {
        holder.bindView()
    }

    inner class SummaryViewHolder(private val binding: AdapterSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bindView() {
            val summary = summaryList[adapterPosition]
            with(binding) {
                summary.apply {
                    textNo.text = String.format(Locale.getDefault(), "%d", adapterPosition + 1)
                    textMatter.text = matterIdDesc ?: ""
                    textTimeTicket.text = timeTicketNumber ?: Constants.NA
                    textTimeTicketDate.text = sTimeTicketDate ?: Constants.NA
                    textBookedHours.text = timeTicketHours.toString()
                    textBookedAmount.text = timeTicketAmount.toDollar()
                    textBillType.text = billType ?: Constants.NA
                    textDescription.text = timeTicketDescription ?: Constants.NA
                    textClientName.text = clientIdDesc ?: Constants.NA
                    textMatterName.text = matterIdDesc ?: Constants.NA
                    if (taskCode.isNullOrEmpty()) {
                        textTaskCode.visibility = View.GONE
                        textTaskCodeLabel.visibility = View.GONE
                    } else {
                        textTaskCode.text = taskCode
                        textTaskCode.visibility = View.VISIBLE
                        textTaskCodeLabel.visibility = View.VISIBLE
                    }
                    if (activityCode.isNullOrEmpty()) {
                        textActivityCode.visibility = View.GONE
                        textActivityCodeLabel.visibility = View.GONE
                    } else {
                        textActivityCode.text = activityCode
                        textActivityCode.visibility = View.VISIBLE
                        textActivityCodeLabel.visibility = View.VISIBLE
                    }

                    if (isExpand) {
                        commonUtils.expandView(lytExpandView)
                        imgArrow.setImageResource(R.drawable.ic_arrow_drop_down)
                        verticalView2.visibility = View.GONE
                        verticalView3.visibility = View.GONE
                    } else {
                        commonUtils.collapseView(lytExpandView)
                        imgArrow.setImageResource(R.drawable.ic_arrow_right)
                        verticalView2.visibility = View.VISIBLE
                        verticalView3.visibility = View.VISIBLE
                    }
                    imgArrow.setOnClickListener {
                        if (isExpand) {
                            this.isExpand = false
                        } else {
                            for (value in summaryList) {
                                value.isExpand = false
                            }
                            this.isExpand = true
                        }
                        notifyDataSetChanged()
                    }
                    btnEdit.setOnClickListener {
                        summary.let { it1 ->
                            onViewClick.invoke(
                                it1,
                                Constants.ACTION_EDIT
                            )
                        }
                    }
                    btnCopy.setOnClickListener {
                        summary.let { it1 ->
                            onViewClick.invoke(
                                it1,
                                Constants.ACTION_COPY
                            )
                        }
                    }
                    btnDelete.setOnClickListener {
                        summary.let { it1 ->
                            onViewClick.invoke(
                                it1,
                                Constants.ACTION_DELETE
                            )
                        }
                    }
                }
                if (adapterPosition == ((summaryList.size) - 1)) {
                    val params = horizontalView2.layoutParams
                    params.height = 3
                    horizontalView2.layoutParams = params
                }
            }
        }
    }

    private inner class SummaryFilter(private val adapter: SummaryAdapter) :
        Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filterResults = FilterResults()
            if (constraint.isEmpty()) {
                filterResults.values = filterList
                filterResults.count = filterList.size
            } else {
                val filteredList: MutableList<TimeTicketSummaryResponse> = mutableListOf()
                val filterPattern = constraint.toString().lowercase(Locale.getDefault())
                for (item in filterList) {
                    if ((item.timeTicketNumber?.lowercase(Locale.getDefault())
                            ?.contains(filterPattern) == true)
                        || (item.sTimeTicketDate?.lowercase(Locale.getDefault())
                            ?.contains(filterPattern) == true)
                        || (item.clientId?.lowercase(Locale.getDefault())
                            ?.contains(filterPattern) == true)
                        || (item.clientIdDesc?.lowercase(Locale.getDefault())
                            ?.contains(filterPattern) == true)
                        || (item.matterNumber?.lowercase(Locale.getDefault())
                            ?.contains(filterPattern) == true)
                        || (item.matterIdDesc?.lowercase(Locale.getDefault())
                            ?.contains(filterPattern) == true)
                        || (item.billType?.lowercase(Locale.getDefault())
                            ?.contains(filterPattern) == true)
                        || (item.timeTicketDescription?.lowercase(Locale.getDefault())
                            ?.contains(filterPattern) == true)
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
            adapter.summaryList = results.values as MutableList<TimeTicketSummaryResponse>
            adapter.notifyDataSetChanged()
        }
    }
}