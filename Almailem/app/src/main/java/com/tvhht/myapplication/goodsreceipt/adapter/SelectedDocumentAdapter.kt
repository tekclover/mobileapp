package com.tvhht.myapplication.goodsreceipt.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tvhht.myapplication.databinding.AdapterSelectedDocumentBinding
import com.tvhht.myapplication.goodsreceipt.model.SelectedDocumentResponse
import com.tvhht.myapplication.putaway.InfoCustomDialog

class SelectedDocumentAdapter(
    private var selectedDocumentList: List<SelectedDocumentResponse>,
    private val onDocumentSelected: (receipt: SelectedDocumentResponse) -> Unit
) :
    RecyclerView.Adapter<SelectedDocumentAdapter.SelectedDocumentViewModel>() {
    private var ctx: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedDocumentViewModel {
        ctx = parent.context
        val binding: AdapterSelectedDocumentBinding =
            AdapterSelectedDocumentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return SelectedDocumentViewModel(binding)
    }

    override fun getItemCount(): Int {
        return selectedDocumentList.size
    }

    override fun onBindViewHolder(holder: SelectedDocumentViewModel, position: Int) {
        holder.bindView()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(updateList: List<SelectedDocumentResponse>) {
        this.selectedDocumentList = updateList
        notifyDataSetChanged()
    }

    inner class SelectedDocumentViewModel(val binding: AdapterSelectedDocumentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView() {
            val selectedDocument = selectedDocumentList[adapterPosition]
            with(binding) {
                with(selectedDocument) {
                    txtCell1.text = partnerItemBarcode ?: ""
                    txtCell2.text = manufacturerName ?: ""
                    txtCell3.text = itemCode ?: ""
                    /* txtCell4.text = orderQty.toString()
                     txtCell5.text =
                         String.format(Locale.getDefault(), "%d", (inventoryQuantity ?: 0).toInt())*/
                }
            }
            binding.txtCell3.setOnClickListener {
                val infoDialog = InfoCustomDialog(
                    ctx,
                    "",
                    selectedDocument.manufacturerName ?: "",
                    selectedDocument.itemCode ?: "",
                    selectedDocument.itemDescription ?: ""
                )
                if (infoDialog.isShowing.not()) {
                    infoDialog.show()
                }
            }
            binding.root.setOnClickListener {
                onDocumentSelected.invoke(selectedDocument)
                /*val intent = Intent(ctx, ScanBarcodeActivity::class.java)
                intent.putExtra("SELECTED_DOCUMENT", selectedDocument)
                ctx?.startActivity(intent)*/
            }
        }
    }
}