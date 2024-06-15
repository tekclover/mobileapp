package com.clara.clientportal.ui.invoice.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.clara.clientportal.R
import com.clara.clientportal.databinding.InvoiceRowViewBinding
import com.clara.clientportal.model.InvoiceResponse
import com.clara.clientportal.utils.CommonUtils
import com.clara.clientportal.utils.Constants
import com.clara.clientportal.utils.PreferenceHelper
import com.clara.clientportal.utils.toDollar

class InvoiceAdapter(
    private val invoiceList: List<InvoiceResponse>,
    private val commonUtils: CommonUtils,
    private val preferenceHelper: PreferenceHelper,
    private val onViewClick: (invoice: InvoiceResponse, isPayment: Boolean) -> Unit
) :
    RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder>() {
    private var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        context = parent.context
        val binding: InvoiceRowViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.invoice_row_view,
            parent,
            false
        )
        return InvoiceViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return invoiceList.size
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        holder.bindView()
    }

    inner class InvoiceViewHolder(private val binding: InvoiceRowViewBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        @SuppressLint("NotifyDataSetChanged")
        fun bindView() {
            val invoice = invoiceList[adapterPosition]
            with(binding) {
                with(invoice) {
                    textMatterNo.text = this.matterNumber ?: ""
                    textDate.text = commonUtils.formatDate(
                        this.invoiceDate ?: "",
                        Constants.YYYY_MM_DD,
                        Constants.MM_DD_YYYY
                    )
                    textStatus.text = commonUtils.getStatus(this.statusId ?: -1)
                    textInvoiceNo.text = this.invoiceNumber ?: ""
                    textInvoiceBillAmount.text = this.invoiceAmount.toDollar()
                    if (isExpand) {
                        commonUtils.expandView(
                            if (preferenceHelper.isTablet().not()) lytInvoiceBillAmount else null
                        )
                    } else {
                        commonUtils.collapseView(
                            if (preferenceHelper.isTablet().not()) lytInvoiceBillAmount else null
                        )
                    }
                    if (preferenceHelper.isTablet().not()) {
                        root.setOnClickListener {
                            if (isExpand) {
                                this.isExpand = false
                            } else {
                                for (value in invoiceList) {
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