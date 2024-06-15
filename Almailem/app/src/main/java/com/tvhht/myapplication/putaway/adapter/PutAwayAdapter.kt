package com.tvhht.myapplication.putaway.adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tvhht.myapplication.R
import com.tvhht.myapplication.putaway.InfoCustomDialog
import com.tvhht.myapplication.putaway.PutAwayDetailsActivity

import com.tvhht.myapplication.putaway.model.PutAwayCombineModel
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import kotlinx.android.synthetic.main.list_putaway_cell.view.*

class PutAwayAdapter(val ctx: Context, val data: List<PutAwayCombineModel>) :
    RecyclerView.Adapter<PutAwayAdapter.ViewHolder>() {


    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PutAwayAdapter.ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_putaway_list, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: PutAwayAdapter.ViewHolder, position: Int) {
        holder.bindItems(data[position])

        holder.sfr_mku.setOnClickListener {

            val cdd = InfoCustomDialog(
                ctx,
                "",
                data[position].manufacturerName,
                data[position].itemCode,
                data[position].itemDescription
            )
            cdd.show()
        }


        holder.itemView.setOnClickListener{

            WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
            ).savePutAwayInfo(data[position])

            val myIntent = Intent(ctx, PutAwayDetailsActivity::class.java)
            ctx.startActivity(myIntent)
        }
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return data.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val palletID = itemView.txt_cell_1
        val sfr_mku = itemView.txt_cell_2
        val bin_no = itemView.txt_cell_3

        fun bindItems(model: PutAwayCombineModel) {
            palletID.text = model.barcodeId.toString()
            sfr_mku.text = model.itemCode
            bin_no.text = model.proposedStorageBin
        }

    }
}