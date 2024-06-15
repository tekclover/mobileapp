package com.clara.clientportal.ui.notification.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.clara.clientportal.R
import com.clara.clientportal.databinding.NotificationViewBinding
import com.clara.clientportal.enums.NotificationOrderType
import com.clara.clientportal.enums.NotificationOrderTypeIcon
import com.clara.clientportal.model.NotificationResponse

class NotificationAdapter(
    private val notificationList: List<NotificationResponse>,
    private val isFromMenu: Boolean,
    private val isFromTab: Boolean,
    private val onMessageClick: (orderType: String) -> Unit
) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    private var mContext: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        mContext = parent.context
        val binding: NotificationViewBinding =
            NotificationViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bindView()
    }

    inner class NotificationViewHolder(private val binding: NotificationViewBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        @SuppressLint("DiscouragedApi")
        fun bindView() {
            val notification = notificationList[adapterPosition]
            with(binding) {
                with(notification) {
                    title?.let {
                        notificationHeader.text = it
                    }
                    message?.let {
                        notificationMessage.text = it
                    }
                    orderType?.let {
                        val orderTypeIcon = when (it) {
                            NotificationOrderType.MATTER.orderType -> NotificationOrderTypeIcon.MATTER_ICON.orderTypeIcon
                            NotificationOrderType.INITIAL.orderType -> NotificationOrderTypeIcon.INITIAL_RETAINER_ICON.orderTypeIcon
                            NotificationOrderType.INVOICE.orderType -> NotificationOrderTypeIcon.INVOICE_ICON.orderTypeIcon
                            NotificationOrderType.PAYMENT_PLAN.orderType -> NotificationOrderTypeIcon.PAYMENT_PLAN_ICON.orderTypeIcon
                            NotificationOrderType.CHECKLIST.orderType -> NotificationOrderTypeIcon.CHECKLIST_ICON.orderTypeIcon
                            NotificationOrderType.DOCUMENT_UPLOAD.orderType -> NotificationOrderTypeIcon.DOCUMENT_UPLOAD_ICON.orderTypeIcon
                            NotificationOrderType.RECEIPT.orderType -> NotificationOrderTypeIcon.RECEIPT_ICON.orderTypeIcon
                            else -> ""
                        }
                        mContext?.let { ctx ->
                            val drawableResourceId: Int? =
                                ctx.resources?.getIdentifier(
                                    orderTypeIcon,
                                    "drawable",
                                    ctx.packageName
                                )
                            drawableResourceId?.let { it1 -> imgNotification.setImageResource(it1) }
                        }
                    }
                    mContext?.let {
                        val isHighLight = when {
                            isFromMenu -> menu
                            isFromTab -> tab
                            else -> false
                        }
                        lytNotificationParent.setBackgroundColor(
                            ContextCompat.getColor(
                                it,
                                if (isHighLight?.not() == true) R.color.color_blue_light else R.color.white
                            )
                        )
                    }
                    root.setOnClickListener {
                        orderType?.let { it1 -> onMessageClick.invoke(it1) }
                    }
                }
            }
        }
    }
}