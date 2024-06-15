package com.tvhht.myapplication.annual.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tvhht.myapplication.R
import com.tvhht.myapplication.annual.model.PeriodicLine
import com.tvhht.myapplication.annual.utils.PalletIDCustomDialog
import kotlinx.android.synthetic.main.adapter_perpetual_confirm.view.*


class AnnualConfirmAdapter(val ctx: Context, val data: List<PeriodicLine>) :
    RecyclerView.Adapter<AnnualConfirmAdapter.ViewHolder>() {


    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_perpetual_confirm, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(data[position])


        holder.itemView.setOnClickListener {

            val cdd = PalletIDCustomDialog(
                ctx,
                "Verify Barcode",
                data!![position]
            )
            cdd.show()
        }
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return data.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val barcode = itemView.txt_cell_1
        val partNo = itemView.txt_cell_2
        val inventoryQty = itemView.txt_cell_3
        val countedNo = itemView.txt_cell_4
        val seletecd = itemView.txt_cell_5


        fun bindItems(model: PeriodicLine) {

            barcode.text = model.barcodeId
            partNo.text = model.itemCode
            inventoryQty.text = model.inventoryQuantity.toString()

            if (model.countedQty == null) {
                countedNo.text = "0"
            } else
                countedNo.text = model.countedQty.toString()


            val toString = countedNo.text.toString()

         //   if()
            seletecd.isChecked = toString.toInt() > 0 || (model.referenceField5?.isNotEmpty() == true && model.referenceField5.equals(
                "YES"
            ))


        }

    }
}