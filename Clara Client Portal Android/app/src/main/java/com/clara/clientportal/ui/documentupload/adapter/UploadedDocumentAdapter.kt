package com.clara.clientportal.ui.documentupload.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.clara.clientportal.R
import com.clara.clientportal.databinding.DocumentUploadRowViewBinding
import com.clara.clientportal.model.DocumentUploadedResponse
import com.clara.clientportal.utils.CommonUtils
import com.clara.clientportal.utils.Constants
import com.clara.clientportal.utils.PreferenceHelper

class UploadedDocumentAdapter(
    private val uploadedDocumentList: List<DocumentUploadedResponse>,
    private val commonUtils: CommonUtils,
    private val preferenceHelper: PreferenceHelper,
    private val onViewDocument: (documentUploaded: DocumentUploadedResponse) -> Unit
) :
    RecyclerView.Adapter<UploadedDocumentAdapter.UploadedDocumentViewHolder>() {
    private var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadedDocumentViewHolder {
        context = parent.context
        val binding: DocumentUploadRowViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.document_upload_row_view,
            parent,
            false
        )
        return UploadedDocumentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return uploadedDocumentList.size
    }

    override fun onBindViewHolder(holder: UploadedDocumentViewHolder, position: Int) {
        holder.bindView()
    }

    inner class UploadedDocumentViewHolder(private val binding: DocumentUploadRowViewBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        @SuppressLint("NotifyDataSetChanged")
        fun bindView() {
            val document = uploadedDocumentList[adapterPosition]
            with(binding) {
                with(document) {
                    textMatterNo.text = this.matterNumber ?: ""
                    textReceivedDate.text = commonUtils.formatDate(
                        this.createdOn ?: "",
                        Constants.YYYY_MM_DD_T_HH_MM_SS_SSS_Z,
                        Constants.MM_DD_YYYY
                    )
                    textDocumentName.text = this.documentUrl ?: ""
                    textUploadedBy.text = context?.resources?.getString(
                        if ((statusId?.toInt()
                                ?: -1) == Constants.STATUS_ID
                        ) R.string.you else R.string.m_r
                    )
                    if (isExpand) {
                        commonUtils.expandView(
                            if (preferenceHelper.isTablet()
                                    .not()
                            ) lytDocumentUploadExpandView else null
                        )
                    } else {
                        commonUtils.collapseView(
                            if (preferenceHelper.isTablet()
                                    .not()
                            ) lytDocumentUploadExpandView else null
                        )
                    }

                    imgDocumentUploadView.setOnClickListener {
                        onViewDocument(this)
                    }
                    if (preferenceHelper.isTablet().not()) {
                        root.setOnClickListener {
                            if (isExpand) {
                                this.isExpand = false
                            } else {
                                for (value in uploadedDocumentList) {
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