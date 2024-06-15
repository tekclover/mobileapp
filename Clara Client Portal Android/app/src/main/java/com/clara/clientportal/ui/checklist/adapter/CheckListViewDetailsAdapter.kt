package com.clara.clientportal.ui.checklist.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.clara.clientportal.R
import com.clara.clientportal.databinding.CheckListViewDetailsRowViewBinding
import com.clara.clientportal.model.MatterDocLists
import com.clara.clientportal.utils.CommonUtils
import com.clara.clientportal.utils.Constants
import com.clara.clientportal.utils.PreferenceHelper

class CheckListViewDetailsAdapter(
    private val matterDocLists: List<MatterDocLists>,
    private val commonUtils: CommonUtils,
    private val preferenceHelper: PreferenceHelper,
    private val onViewClick: (matterDocList: MatterDocLists, isUpload: Boolean) -> Unit
) :
    RecyclerView.Adapter<CheckListViewDetailsAdapter.CheckListViewDetailsViewHolder>() {
    private var context: Context? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CheckListViewDetailsViewHolder {
        context = parent.context
        val binding: CheckListViewDetailsRowViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.check_list_view_details_row_view,
            parent,
            false
        )
        return CheckListViewDetailsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return matterDocLists.size
    }

    override fun onBindViewHolder(holder: CheckListViewDetailsViewHolder, position: Int) {
        holder.bindView()
    }

    inner class CheckListViewDetailsViewHolder(private val binding: CheckListViewDetailsRowViewBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        @SuppressLint("NotifyDataSetChanged")
        fun bindView() {
            val quotation = matterDocLists[adapterPosition]
            with(binding) {
                with(quotation) {
                    textMatterNoLabel?.text = context?.resources?.getString(R.string.document)
                    textMatterDateLabel?.text = context?.resources?.getString(R.string.status)
                    textQuoteNoLabel?.text = context?.resources?.getString(R.string.attachment)
                    textQuoteStatusLabel?.text = context?.resources?.getString(R.string.date)
                    textMatterNo.text = this.documentName ?: ""
                    textQuoteStatus.text = commonUtils.formatDate(
                        this.updatedOn ?: "",
                        Constants.YYYY_MM_DD_T_HH_MM_SS_SSS_Z,
                        Constants.MM_DD_YYYY
                    )
                    textQuoteNo.text =
                        if (this.documentUrl.isNullOrEmpty().not()) this.documentUrl else "NA"
                    textMatterDate.text = when {
                        (this.statusId ?: -1) == 23 -> context?.resources?.getString(R.string.send)
                        (this.statusId
                            ?: -1) == 22 -> context?.resources?.getString(R.string.pending)

                        else -> context?.resources?.getString(R.string.uploaded)
                    }
                 /*   if (isExpand) {
                        commonUtils.expandView(
                            if (preferenceHelper.isTablet().not()) lytQuotationExpandView else null
                        )
                        commonUtils.expandView(
                            if (preferenceHelper.isTablet().not()) lytBtnUpload else null
                        )
                        commonUtils.expandView(
                            if (preferenceHelper.isTablet().not()) lytBtnDownload else null
                        )
                    } else {
                        commonUtils.collapseView(
                            if (preferenceHelper.isTablet().not()) lytQuotationExpandView else null
                        )
                        commonUtils.expandView(
                            if (preferenceHelper.isTablet().not()) lytBtnUpload else null
                        )
                        commonUtils.expandView(
                            if (preferenceHelper.isTablet().not()) lytBtnDownload else null
                        )
                    }*/
/*
                    if (preferenceHelper.isTablet().not()) {
                        root.setOnClickListener {
                            if (isExpand) {
                                this.isExpand = false
                            } else {
                                for (value in matterDocLists) {
                                    value.isExpand = false
                                }
                                this.isExpand = true
                            }
                            notifyDataSetChanged()
                        }
                    }
*/
                    btnUpload.setOnClickListener {
                        onViewClick(this, true)
                    }
                    btnDownload.setOnClickListener {
                        onViewClick(this, false)
                    }
                }
            }
        }
    }
}