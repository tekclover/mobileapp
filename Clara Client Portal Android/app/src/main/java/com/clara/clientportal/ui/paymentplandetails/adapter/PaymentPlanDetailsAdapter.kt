package com.clara.clientportal.ui.paymentplandetails.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.clara.clientportal.R
import com.clara.clientportal.databinding.PaymentDetailsRowViewBinding
import com.clara.clientportal.model.PaymentPlanLines
import com.clara.clientportal.utils.CommonUtils
import com.clara.clientportal.utils.Constants
import com.clara.clientportal.utils.toDollar
import java.util.Locale

class PaymentPlanDetailsAdapter(
    private val paymentPlanDetailsList: List<PaymentPlanLines>,
    private val commonUtils: CommonUtils,
    ) :
    RecyclerView.Adapter<PaymentPlanDetailsAdapter.PaymentPlanDetailsViewHolder>() {
    private var context: Context? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PaymentPlanDetailsViewHolder {
        context = parent.context
        val binding: PaymentDetailsRowViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.payment_details_row_view,
            parent,
            false
        )
        return PaymentPlanDetailsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return paymentPlanDetailsList.size
    }

    override fun onBindViewHolder(holder: PaymentPlanDetailsViewHolder, position: Int) {
        holder.bindView()
    }

    inner class PaymentPlanDetailsViewHolder(private val binding: PaymentDetailsRowViewBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun bindView() {
            val paymentPlanDetails = paymentPlanDetailsList[adapterPosition]
            with(binding) {
                with(paymentPlanDetails) {
                    textInstallmentNo.text =
                        String.format(Locale.getDefault(), "%d", this.itemNumber ?: -1)
                    textDueDate.text = commonUtils.formatDate(
                        this.dueDate ?: "",
                        Constants.YYYY_MM_DD,
                        Constants.MM_DD_YYYY
                    )
                    textInstallmentAmount.text = this.dueAmount?.toDouble()?.toDollar()
                    textPaidAmount.text =
                        if (this.paidAmount?.toDouble()?.toDollar().isNullOrEmpty()
                                .not()
                        ) this.paidAmount?.toDouble()?.toDollar() else (0.00.toDollar())
                   /* textBalanceAmount.text =
                        ((this.dueAmount?.toDouble() ?: 0.0) - (this.paidAmount?.toDouble()
                            ?: 0.0)).toDollar()*/
                    textBalanceAmount.text = this.remainingDueNow?.toDouble()?.toDollar()
                }
            }

        }
    }
}