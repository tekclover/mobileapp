package com.tvhht.myapplication.transfers.adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tvhht.myapplication.R
import com.tvhht.myapplication.putaway.InfoCustomDialog
import com.tvhht.myapplication.transfers.InventoryListActivity
import com.tvhht.myapplication.transfers.model.InventoryModel
import com.tvhht.myapplication.transfers.utils.InvQuantityCustomDialogActivity
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import kotlinx.android.synthetic.main.list_putaway_cell.view.*
import kotlinx.android.synthetic.main.list_putaway_cell.view.txt_cell_1
import kotlinx.android.synthetic.main.list_putaway_cell.view.txt_cell_2
import kotlinx.android.synthetic.main.list_putaway_cell.view.txt_cell_3
import kotlinx.android.synthetic.main.list_putaway_cell.view.txt_cell_4
import kotlinx.android.synthetic.main.list_transfers_cell.view.*

class InventoryAdapter(val ctx: Context, val data: List<InventoryModel>) :
    RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {


    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryAdapter.ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.list_transfers_cell, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: InventoryAdapter.ViewHolder, position: Int) {
        holder.bindItems(data[position])

        holder.palletID.setOnClickListener {

            val cdd = InfoCustomDialog(
                ctx,
                "",
                data[position].referenceField9,
                data[position].itemCode,
                data[position].referenceField8
            )
            cdd.show()
        }

        holder.isSelected.setOnClickListener {


            if (holder.isSelected.isChecked) {

                WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).saveInventoryInfo(data[position])


                holder.isSelected.isChecked = true

                val myIntent = Intent(ctx, InvQuantityCustomDialogActivity::class.java)
                ctx.startActivity(myIntent)
            } else {

                val qtyInfo = WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).inventoryInfo

                qtyInfo?.transferQuantity = 0
                qtyInfo?.isSelected = false
                WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).saveInventoryInfo(qtyInfo)
              //  holder.isSelected.isChecked = false

                if (ctx is InventoryListActivity) {
                    ctx.getUpdatedData()
                }
            }


        }
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return data.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val itemCode = itemView.txt_cell_1
        val palletID = itemView.txt_cell_2
        val inventoryQty = itemView.txt_cell_3
        val transferQty = itemView.txt_cell_4
        val isSelected = itemView.txt_cell_5


        fun bindItems(model: InventoryModel) {
            itemCode.text = model.itemCode.toString()
            palletID.text = model.packBarcodes.toString()
            inventoryQty.text = model.inventoryQuantity.toString()
            if (model.transferQuantity != null)
                transferQty.text = model.transferQuantity.toString()
            isSelected.isChecked = model.isSelected

        }

    }
}