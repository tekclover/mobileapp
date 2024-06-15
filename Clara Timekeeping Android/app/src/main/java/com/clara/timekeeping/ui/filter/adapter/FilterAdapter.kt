package com.clara.timekeeping.ui.filter.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.clara.timekeeping.R
import com.clara.timekeeping.databinding.AdapterFilterBinding
import com.clara.timekeeping.databinding.AdapterFilterSingleSelectBinding
import com.clara.timekeeping.model.SearchResult
import java.util.Locale

class FilterAdapter(
    private var searchList: MutableList<SearchResult>,
    private val isSingleSelect: Boolean,
    private var addOrRemoveItem: (searchResult: SearchResult, isAddItem: Boolean) -> Unit,
    private var filteredList: (filterList: MutableList<SearchResult>) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private val filterList: MutableList<SearchResult> = searchList
    private lateinit var mSearchFilter: SearchFilter

    companion object {
        private const val SINGLE_SELECTION = 0
        private const val MULTI_SELECTION = 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            0 -> {
                val singleSectionHolder = holder as SingleSelectViewHolder
                singleSectionHolder.bindView()
            }

            1 -> {
                val multiSelectionHolder = holder as MultiSelectViewHolder
                multiSelectionHolder.bindView()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (getItemViewType(viewType) == SINGLE_SELECTION) {
            val binding: AdapterFilterSingleSelectBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.adapter_filter_single_select,
                parent,
                false
            )
            return SingleSelectViewHolder(binding)
        } else {
            val binding: AdapterFilterBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.adapter_filter,
                parent,
                false
            )
            return MultiSelectViewHolder(binding)
        }
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(updateList: MutableList<SearchResult>) {
        this.searchList = updateList
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        if (!this::mSearchFilter.isInitialized) {
            mSearchFilter = SearchFilter(this@FilterAdapter)
        }
        return mSearchFilter
    }

    override fun getItemViewType(position: Int): Int {
        if (isSingleSelect) {
            return SINGLE_SELECTION
        }
        return MULTI_SELECTION
    }

    inner class MultiSelectViewHolder(private val binding: AdapterFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView() {
            val result = searchList[adapterPosition]
            binding.textFilter.text = result.name ?: ""
            binding.textFilter.setCompoundDrawablesWithIntrinsicBounds(
                (if (result.isChecked) R.drawable.ic_checked else R.drawable.ic_unchecked),
                0,
                0,
                0
            )
            binding.root.setOnClickListener {
               /* if (FilterHelperClass.isAllChecked) {
                    result.isChecked = true
                    addOrRemoveItem.invoke(result, true)
                } else*/ if (result.isChecked) {
                    result.isChecked = false
                    addOrRemoveItem.invoke(result, false)
                } else {
                    result.isChecked = true
                    addOrRemoveItem.invoke(result, true)
                }
                notifyItemChanged(adapterPosition)
            }
        }
    }

    inner class SingleSelectViewHolder(private val binding: AdapterFilterSingleSelectBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bindView() {
            val result = searchList[adapterPosition]
            binding.textFilter.text = result.name ?: ""
            binding.textFilter.setCompoundDrawablesWithIntrinsicBounds(
                (if (result.isChecked) R.drawable.ic_check_circle_checked else R.drawable.ic_circle_check_unchecked),
                0,
                0,
                0
            )
            binding.root.setOnClickListener {
                if (result.isChecked) {
                    result.isChecked = false
                    addOrRemoveItem.invoke(result, false)
                   // result.id?.let { it1 -> addOrRemoveItem.invoke(it1, false, false) }
                } else {
                    for (value in searchList) {
                        value.isChecked = false
                    }
                    result.isChecked = true
                    addOrRemoveItem.invoke(result, true)
                   // result.id?.let { it1 -> addOrRemoveItem.invoke(it1, true, false) }
                }
                notifyDataSetChanged()
            }
        }
    }

    private inner class SearchFilter(private val adapter: FilterAdapter) :
        Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filterResults = FilterResults()
            if (constraint.isEmpty()) {
                filterResults.values = filterList
                filterResults.count = filterList.size
            } else {
                val filteredList: MutableList<SearchResult> = mutableListOf()
                val filterPattern = constraint.toString().lowercase(Locale.getDefault())
                for (item in filterList) {
                    if (item.name?.lowercase(Locale.getDefault())
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
            adapter.searchList = results.values as MutableList<SearchResult>
            adapter.notifyDataSetChanged()
            filteredList.invoke(adapter.searchList)
        }
    }
}