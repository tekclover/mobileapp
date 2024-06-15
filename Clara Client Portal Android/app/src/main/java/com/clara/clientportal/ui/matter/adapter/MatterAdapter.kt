package com.clara.clientportal.ui.matter.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.clara.clientportal.R
import com.clara.clientportal.databinding.MatterRowViewBinding
import com.clara.clientportal.model.MatterResponse
import com.clara.clientportal.utils.CommonUtils
import com.clara.clientportal.utils.Constants

class MatterAdapter(
    private val matterList: List<MatterResponse>,
    private val commonUtils: CommonUtils,
    private val isTablet: Boolean,
    private val onViewClick: (matterNo: String) -> Unit,
) :
    RecyclerView.Adapter<MatterAdapter.MatterViewHolder>() {
    private var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatterViewHolder {
        context = parent.context
        val binding: MatterRowViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.matter_row_view,
            parent,
            false
        )
        return MatterViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return matterList.size
    }

    override fun onBindViewHolder(holder: MatterViewHolder, position: Int) {
        holder.bindView()
    }

    inner class MatterViewHolder(private val binding: MatterRowViewBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        @SuppressLint("NotifyDataSetChanged")
        fun bindView() {
            val matter = matterList[adapterPosition]
            with(binding) {
                with(matter) {
                    textMatterNo.text =
                        when {
                            isExpand || isTablet -> ((this.matterNumber
                                ?: "") + "\n" + (this.matterDescription
                                ?: ""))

                            else -> (this.matterNumber ?: "")
                        }
                    textMatterDate.text = this.caseOpenedDate ?: ""
                    textMatterDate.text = commonUtils.formatDate(
                        this.caseOpenedDate ?: "",
                        Constants.MM_D_YYYY,
                        Constants.MM_DD_YYYY
                    )
                    textMatterStatus.text =
                        commonUtils.getStatus(this.statusId ?: -1)
                    imgMatterView.setOnClickListener {
                        this.matterNumber?.let {
                            onViewClick.invoke(it)
                        }
                    }
                    if (isTablet.not()) {
                        root.setOnClickListener {
                            if (isExpand) {
                                this.isExpand = false
                            } else {
                                for (value in matterList) {
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

    interface MatterAdapterListener {
        fun onViewClick(matterNo: String)
    }
}