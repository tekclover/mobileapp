package com.tvhht.myapplication.quality.adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tvhht.myapplication.R
import com.tvhht.myapplication.databinding.ListQualityDetailsConfirmCellBinding
import com.tvhht.myapplication.quality.model.QualityDetailsModel
import com.tvhht.myapplication.quality.utils.BarcodeIdNewCustomDialog
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref


class QualityDetailsAdapter(private val ctx: Context, val data: List<QualityDetailsModel>) :
    RecyclerView.Adapter<QualityDetailsAdapter.QualityDetailsViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QualityDetailsViewHolder {
        val binding: ListQualityDetailsConfirmCellBinding =
            ListQualityDetailsConfirmCellBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return QualityDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QualityDetailsViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class QualityDetailsViewHolder(val binding: ListQualityDetailsConfirmCellBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItems() {
            val qualityDetails = data[adapterPosition]
            with(binding) {
                with(qualityDetails) {
                    txtCell1.text = manufacturerName
                    txtCell2.text = itemCode
                    txtCell3.text = referenceField6
                    txtCell4.text = pickConfirmQty.toString()
                    txtCell5.setImageDrawable(ctx.let {
                        ContextCompat.getDrawable(
                            it,
                            if (isSelected) R.drawable.ic_check_box_checked else R.drawable.ic_check_box_blank
                        )
                    })
                }
                txtCell5.setOnClickListener {
                    if (qualityDetails.isSelected.not()) {
                        WMSSharedPref.getAppPrefs(
                            WMSApplication.getInstance().applicationContext
                        ).saveQualityInfo(qualityDetails)
                        /* val myIntent = Intent(ctx, QualityCustomDialogActivity::class.java)
                 ctx.startActivity(myIntent)  */
                        // barcodeList.addAll(data)
                        // data[position].referenceField6?.let { it1 -> barcodeList.add(it1) }
                        /* val myIntent = Intent(ctx, HECustomDialog::class.java)
                 myIntent.putExtra("EXTRA_CODE_1", data[position].actualHeNo)
                 myIntent.putExtra("EXTRA_CODE_2",data[position].pickupNumber)
                 myIntent.putExtra("EXTRA_CODE_3",data[position].referenceField6)
                 myIntent.putExtra("EXTRA_CODE_4","Verify HE Number")
                 myIntent.putExtra("EXTRA_CODE_5", barcodeList)
                 ctx.startActivity(myIntent)*/
                        val barcodeList: ArrayList<String> = ArrayList()
                        for (quality in data) {
                            if (quality.isSelected.not()) {
                                quality.referenceField6?.let { it1 -> barcodeList.add(it1) }
                            }
                        }
                        val intent = Intent(ctx, BarcodeIdNewCustomDialog::class.java)
                        intent.putStringArrayListExtra("BARCODE_LIST", barcodeList)
                        intent.putExtra("BARCODE_ID", qualityDetails.referenceField6 ?: "")
                        ctx.startActivity(intent)
                    }
                }
            }
        }
    }
}