package com.tvhht.myapplication.outboundreturns.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tvhht.myapplication.R
import com.tvhht.myapplication.picking.model.PickingQuantityConfirm
import kotlinx.android.synthetic.main.list_picking_details_confirm_cell.view.*


class PickingConfirmAdapter(val ctx: Context, val data: List<PickingQuantityConfirm>) :
    RecyclerView.Adapter<PickingConfirmAdapter.ViewHolder>() {


    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_picking_details_confirm_cell, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(data!![position])


    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return data!!.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(model: PickingQuantityConfirm) {
            val bin_Location = itemView.txt_cell_1
            val palletID = itemView.txt_cell_2
            val inventQty = itemView.txt_cell_3
            val pickedQty = itemView.txt_cell_4
            val seletecd = itemView.txt_cell_5

            bin_Location.text = model.binLocation
            palletID.text = model.barcodeId
            inventQty.text = model.inventoryQty.toString()
            pickedQty.text = model.pickedQty.toString()
            seletecd.isChecked = model.isSelected == true

        }

    }
}