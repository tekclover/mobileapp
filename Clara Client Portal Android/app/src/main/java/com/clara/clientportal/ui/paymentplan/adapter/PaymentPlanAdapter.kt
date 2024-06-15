package com.clara.clientportal.ui.paymentplan.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.clara.clientportal.R
import com.clara.clientportal.databinding.PaymentPlanRowViewBinding
import com.clara.clientportal.model.PaymentPlanResponse
import com.clara.clientportal.utils.CommonUtils
import com.clara.clientportal.utils.Constants
import com.clara.clientportal.utils.PreferenceHelper
import com.clara.clientportal.utils.toDollar

class PaymentPlanAdapter(
    private val paymentPlanList: List<PaymentPlanResponse>,
    private val commonUtils: CommonUtils,
    private val preferenceHelper: PreferenceHelper,
    private val onPaymentPlan: (paymentPlanNo: String, paymentPlanRevisionNo: Int) -> Unit
) :
    RecyclerView.Adapter<PaymentPlanAdapter.PaymentPlanViewHolder>() {
    private var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentPlanViewHolder {
        context = parent.context
        val binding: PaymentPlanRowViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.payment_plan_row_view,
            parent,
            false
        )
        return PaymentPlanViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return paymentPlanList.size
    }

    override fun onBindViewHolder(holder: PaymentPlanViewHolder, position: Int) {
        holder.bindView()
    }

    inner class PaymentPlanViewHolder(private val binding: PaymentPlanRowViewBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        @SuppressLint("NotifyDataSetChanged")
        fun bindView() {
            val paymentPlan = paymentPlanList[adapterPosition]
            with(binding) {
                with(paymentPlan) {
                    textMatterNo.text = this.matterNumber ?: ""
                    textDate.text = commonUtils.formatDate(
                        this.paymentPlanDate ?: "",
                        Constants.YYYY_MM_DD,
                        Constants.MM_DD_YYYY
                    )
                    textStatus.text = commonUtils.getStatus(this.statusId ?: -1)
                    textPaymentPlanNo.text = this.paymentPlanNumber ?: ""
                    textPaymentPlanAmount.text = this.paymentPlanTotalAmount.toDollar()
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
                        onPaymentPlan(this.paymentPlanNumber ?: "", paymentPlanRevisionNo ?: -1)
                    }
                    if (preferenceHelper.isTablet().not()) {
                        root.setOnClickListener {
                            if (isExpand) {
                                this.isExpand = false
                            } else {
                                for (value in paymentPlanList) {
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