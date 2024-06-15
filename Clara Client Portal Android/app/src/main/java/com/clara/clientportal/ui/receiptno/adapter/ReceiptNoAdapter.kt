package com.clara.clientportal.ui.receiptno.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.clara.clientportal.R
import com.clara.clientportal.databinding.PaymentPlanRowViewBinding
import com.clara.clientportal.model.ReceiptNoResponse
import com.clara.clientportal.utils.CommonUtils
import com.clara.clientportal.utils.Constants
import com.clara.clientportal.utils.PreferenceHelper

class ReceiptNoAdapter(
    private val receiptNoList: List<ReceiptNoResponse>,
    private val commonUtils: CommonUtils,
    private val preferenceHelper: PreferenceHelper,
    private val onDownloadClick: (clientId: String, matterNo: String, downloadDocument: String) -> Unit
) :
    RecyclerView.Adapter<ReceiptNoAdapter.ReceiptViewHolder>() {
    private var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        context = parent.context
        val binding: PaymentPlanRowViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.payment_plan_row_view,
            parent,
            false
        )
        return ReceiptViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return receiptNoList.size
    }

    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        holder.bindView()
    }

    inner class ReceiptViewHolder(private val binding: PaymentPlanRowViewBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        @SuppressLint("NotifyDataSetChanged")
        fun bindView() {
            val receiptNo = receiptNoList[adapterPosition]
            with(binding) {
                with(receiptNo) {
                    textMatterNoLabel?.text = context?.resources?.getString(R.string.document_type)
                    textStatusLabel?.text = context?.resources?.getString(R.string.receipt_no)
                    textPaymentPlanNoLabel?.text = context?.resources?.getString(R.string.type)
                    textDateLabel?.text = context?.resources?.getString(R.string.date)
                    textPaymentPlanAmountLabel?.text =
                        context?.resources?.getString(R.string.status)
                    context?.let {
                        Glide.with(it).load(R.drawable.ic_download).into(imgPaymentPlanView)
                    }
                    textMatterNo.text = this.documentType ?: ""
                    textDate.text = commonUtils.formatDate(
                        this.receiptDate ?: "",
                        Constants.YYYY_MM_DD,
                        Constants.MM_DD_YYYY
                    )
                    textStatus.text = this.receiptNo ?: ""
                    textPaymentPlanNo.text = this.receiptType ?: ""
                    textPaymentPlanAmount.text = commonUtils.getStatus(this.statusId ?: -1)
                    if (isExpand) {
                        commonUtils.expandView(
                            if (preferenceHelper.isTablet()
                                    .not()
                            ) lytPaymentPlanExpandView else null
                        )
                    } else {
                        commonUtils.collapseView(
                            if (preferenceHelper.isTablet()
                                    .not()
                            ) lytPaymentPlanExpandView else null
                        )
                    }

                    imgPaymentPlanView.setOnClickListener {
                        onDownloadClick(
                            this.clientId ?: "",
                            this.matterNumber ?: "",
                            this.referenceField8 ?: ""
                        )
                    }
                    if (preferenceHelper.isTablet().not()) {
                        root.setOnClickListener {
                            if (isExpand) {
                                this.isExpand = false
                            } else {
                                for (value in receiptNoList) {
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