package com.clara.timekeeping.ui.notification.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.clara.timekeeping.R
import com.clara.timekeeping.databinding.NotificationViewBinding
import com.clara.timekeeping.model.NotificationResponse
import com.clara.timekeeping.utils.CommonUtils
import com.clara.timekeeping.utils.Constants

class NotificationAdapter(
    private val notificationList: List<NotificationResponse>,
    private val commonUtils: CommonUtils
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
                    createdOn?.let {
                        notificationDate.text = commonUtils.formatDate(
                            it,
                            Constants.YYYY_MM_DD_T_HH_MM_SS_SSS_Z,
                            Constants.MM_DD_YYYY
                        )
                    }
                    mContext?.let {
                        lytNotificationParent.setBackgroundColor(
                            ContextCompat.getColor(
                                it,
                                if (menu?.not() == true) R.color.color_blue_light else R.color.white
                            )
                        )
                    }
                }
            }
        }
    }
}