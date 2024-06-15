package com.clara.clientportal.ui.quotation.adpter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.clara.clientportal.R
import com.clara.clientportal.databinding.InitialRetainerRowViewBinding
import com.clara.clientportal.model.QuotationResponse
import com.clara.clientportal.utils.CommonUtils
import com.clara.clientportal.utils.Constants
import com.clara.clientportal.utils.PreferenceHelper

class QuotationAdapter(
    private val quotationList: List<QuotationResponse>,
    private val commonUtils: CommonUtils,
    private val preferenceHelper: PreferenceHelper,
    private val onViewClick: (quotation: QuotationResponse, isPayment: Boolean) -> Unit
) :
    RecyclerView.Adapter<QuotationAdapter.QuotationViewHolder>() {
    private var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuotationViewHolder {
        context = parent.context
        val binding: InitialRetainerRowViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.initial_retainer_row_view,
            parent,
            false
        )
        return QuotationViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return quotationList.size
    }

    override fun onBindViewHolder(holder: QuotationViewHolder, position: Int) {
        holder.bindView()
    }

    inner class QuotationViewHolder(private val binding: InitialRetainerRowViewBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        @SuppressLint("NotifyDataSetChanged")
        fun bindView() {
            val quotation = quotationList[adapterPosition]
            with(binding) {
                with(quotation) {
                    textMatterNo.text = this.matterNumber ?: ""
                    textMatterDate.text = commonUtils.formatDate(
                        this.quotationDate ?: "",
                        Constants.YYYY_MM_DD_T_HH_MM_SS_SSS_Z,
                        Constants.MM_DD_YYYY
                    )
                    textQuoteNo.text = this.quotationNo ?: ""
                    binding.textQuoteStatus.text = commonUtils.getStatus(this.statusId ?: -1)
                    if (isExpand) {
                        commonUtils.expandView(
                            if (preferenceHelper.isTablet().not()) lytQuotationExpandView else null
                        )
                    } else {
                        commonUtils.collapseView(
                            if (preferenceHelper.isTablet().not()) lytQuotationExpandView else null
                        )
                    }
                    if (preferenceHelper.isTablet().not()) {
                        root.setOnClickListener {
                            if (isExpand) {
                                this.isExpand = false
                            } else {
                                for (value in quotationList) {
                                    value.isExpand = false
                                }
                                this.isExpand = true
                            }
                            notifyDataSetChanged()
                        }
                    }
                    imgPayment.setOnClickListener {
                        onViewClick(this, true)
                    }
                    imgView.setOnClickListener {
                        onViewClick(this, false)
                    }
                }
            }
        }
    }
}