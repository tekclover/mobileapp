package com.tvhht.myapplication.putaway.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tvhht.myapplication.R
import com.tvhht.myapplication.putaway.model.BinQuantityConfirm
import kotlinx.android.synthetic.main.list_putaway_confirm_cell.view.*
import java.util.Locale

class PutAwayConfirmAdapter(val ctx: Context, val data: List<BinQuantityConfirm>) :
    RecyclerView.Adapter<PutAwayConfirmAdapter.ViewHolder>() {


    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PutAwayConfirmAdapter.ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.list_putaway_confirm_cell, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: PutAwayConfirmAdapter.ViewHolder, position: Int) {
        holder.bindItems(data!![position])


    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return data!!.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(model: BinQuantityConfirm) {
            val bin_Location = itemView.txt_cell_1
            val putaway_qty = itemView.txt_cell_2
            val sPIcked = itemView.txt_cell_3

            bin_Location.text = model.binLocation
            putaway_qty.text = String.format(Locale.getDefault(),"%d",(model.putawayConfirmedQty ?:0).toInt())
            sPIcked.isChecked= model.isSelected == true

        }

    }
}