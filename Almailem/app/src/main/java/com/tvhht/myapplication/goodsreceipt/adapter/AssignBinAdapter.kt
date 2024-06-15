package com.tvhht.myapplication.goodsreceipt.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tvhht.myapplication.R
import com.tvhht.myapplication.databinding.AdapterAssignBinnerBinding
import com.tvhht.myapplication.goodsreceipt.model.HHTUser

class AssignBinAdapter(
    private val userList: List<HHTUser>,
    private val onSelectedUser: (user: HHTUser) -> Unit
) :
    RecyclerView.Adapter<AssignBinAdapter.AssignBinViewModel>() {
    private var ctx: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignBinViewModel {
        ctx = parent.context
        val binding: AdapterAssignBinnerBinding =
            AdapterAssignBinnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignBinViewModel(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: AssignBinViewModel, position: Int) {
        holder.bindView()
    }

    inner class AssignBinViewModel(val binding: AdapterAssignBinnerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bindView() {
            val userDetails = userList[adapterPosition]
            with(binding) {
                with(userDetails) {
                    textHhtUserId.text = userId ?: ""
                    textHhtUserName.text = userName ?: ""
                    imgCheck.setImageDrawable(ctx?.let {
                        ContextCompat.getDrawable(
                            it,
                            if (isChecked) R.drawable.ic_check_box_checked else R.drawable.ic_check_box_blank
                        )
                    })
                    binding.root.setOnClickListener {
                        if (this.isChecked) {
                            this.isChecked = false
                        } else {
                            for (value in userList) {
                                value.isChecked = false
                            }
                            this.isChecked = true
                            onSelectedUser.invoke(this)
                        }
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }
}