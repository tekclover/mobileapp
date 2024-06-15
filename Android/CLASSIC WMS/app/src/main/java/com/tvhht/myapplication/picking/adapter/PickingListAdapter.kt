package com.tvhht.myapplication.picking.adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tvhht.myapplication.R
import com.tvhht.myapplication.picking.PickingDetailsActivity

import com.tvhht.myapplication.picking.model.PickingCombineResponse

import com.tvhht.myapplication.putaway.InfoCustomDialog
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import kotlinx.android.synthetic.main.list_picking_details_cell.view.*


class PickingListAdapter(val ctx: Context, val data: List<PickingCombineResponse>) :
    RecyclerView.Adapter<PickingListAdapter.ViewHolder>() {


    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PickingListAdapter.ViewHolder {
        val v =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_picking_details_cell, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: PickingListAdapter.ViewHolder, position: Int) {
        holder.bindItems(data[position])

        holder.sfr_mku.setOnClickListener {

            val cdd = InfoCustomDialog(
                ctx,
                "",
                data[position].manufacturerPartNo,
                data[position].itemCode,
                data[position].description,
            )
            cdd.show()
        }

        holder.itemView.setOnClickListener {

            WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
            ).savePickingInfo(data[position])

            val myIntent = Intent(ctx, PickingDetailsActivity::class.java)
            myIntent.putExtra("DEFAULT_BIN_NO",data[position].proposedStorageBin)
            myIntent.putExtra("DEFAULT_PALLET_NO",data[position].proposedPackBarCode)
            ctx.startActivity(myIntent)
        }
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return data.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val bin_no = itemView.txt_cell_1
        val sfr_mku = itemView.txt_cell_2
        val qty = itemView.txt_cell_3


        fun bindItems(model: PickingCombineResponse) {

            sfr_mku.text = model.itemCode
            bin_no.text = model.proposedStorageBin
            qty.text = model.pickToQty.toString()

        }

    }
}