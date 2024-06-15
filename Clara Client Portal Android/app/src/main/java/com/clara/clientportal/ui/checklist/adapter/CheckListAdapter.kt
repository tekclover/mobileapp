package com.clara.clientportal.ui.checklist.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.clara.clientportal.R
import com.clara.clientportal.databinding.ChecklistRowViewBinding
import com.clara.clientportal.model.CheckListResponse
import com.clara.clientportal.utils.CommonUtils
import com.clara.clientportal.utils.Constants
import com.clara.clientportal.utils.PreferenceHelper

class CheckListAdapter(
    private val documentCheckList: List<CheckListResponse>,
    private val commonUtils: CommonUtils,
    private val preferenceHelper: PreferenceHelper,
    private val onViewClick: (checkListResponse: CheckListResponse) -> Unit
) :
    RecyclerView.Adapter<CheckListAdapter.CheckListViewHolder>() {
    private var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckListViewHolder {
        context = parent.context
        val binding: ChecklistRowViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.checklist_row_view,
            parent,
            false
        )
        return CheckListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return documentCheckList.size
    }

    override fun onBindViewHolder(holder: CheckListViewHolder, position: Int) {
        holder.bindView()
    }

    inner class CheckListViewHolder(private val binding: ChecklistRowViewBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        @SuppressLint("NotifyDataSetChanged")
        fun bindView() {
            val checkList = documentCheckList[adapterPosition]
            with(binding) {
                with(checkList) {
                    textMatterNo.text = this.matterNumber ?: ""
                    textCheckListDate.text = commonUtils.formatDate(
                        this.updatedOn ?: "",
                        Constants.YYYY_MM_DD_T_HH_MM_SS_SSS_Z,
                        Constants.MM_DD_YYYY
                    )
                    textCheckListStatus.text = when {
                        (this.statusId ?: -1) == 23 -> context?.resources?.getString(R.string.send)
                        (this.statusId
                            ?: -1) == 22 -> context?.resources?.getString(R.string.received)

                        else -> ""
                    }
                    imgCheckListView.setOnClickListener {
                        onViewClick(this)
                    }
                    if (preferenceHelper.isTablet().not()) {
                        root.setOnClickListener {
                            if (isExpand) {
                                this.isExpand = false
                            } else {
                                for (value in documentCheckList) {
                                    value.isExpand = false
                                }
                                this.isExpand = true
                            }
                            notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }
}