package com.tvhht.myapplication.reports.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tvhht.myapplication.R
import com.tvhht.myapplication.putaway.InfoCustomDialog
import com.tvhht.myapplication.reports.model.ReportResponse
import kotlinx.android.synthetic.main.list_report_storage_bin_cell.view.*


class ReportBinAdapter(val ctx: Context, val data: List<ReportResponse>) :
    RecyclerView.Adapter<ReportBinAdapter.ViewHolder>() {


    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReportBinAdapter.ViewHolder {
        val v =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_report_storage_bin_cell, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ReportBinAdapter.ViewHolder, position: Int) {
        holder.bindItems(data[position])

        var intSno= position+1

        holder.s_no.text = intSno.toString()

        holder.item_code.setOnClickListener {

            val cdd = InfoCustomDialog(
                ctx,
                "",
                data[position].referenceField9,
                data[position].itemCode,
                data[position].referenceField8
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


        val s_no = itemView.txt_cell_1
        val palletID = itemView.txt_cell_2
        val item_code = itemView.txt_cell_3
        val qty = itemView.txt_cell_4


        fun bindItems(model: ReportResponse) {
            palletID.text = model.packBarcodes
            item_code.text = model.itemCode
            qty.text = model.inventoryQuantity.toString()

        }

    }
}