package com.tvhht.myapplication.stock.adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tvhht.myapplication.R
import com.tvhht.myapplication.annual.AnnualConfirmActivity
import com.tvhht.myapplication.putaway.InfoCustomDialog
import com.tvhht.myapplication.stock.PerpetualConfirmActivity
import com.tvhht.myapplication.stock.model.PerpetualLine
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import kotlinx.android.synthetic.main.list_perpetual_detail_cell.view.*


class PerpetualDetailAdapter(val ctx: Context, val data: List<PerpetualLine>) :
    RecyclerView.Adapter<PerpetualDetailAdapter.ViewHolder>() {


    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_perpetual_detail_cell, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(data[position])

        holder.itemcode.setOnClickListener {

            val cdd = InfoCustomDialog(
                ctx,
                "",
                data[position].manufacturerPartNo,
                data[position].itemCode,
                data[position].itemDesc,
            )
            cdd.show()
        }

//        holder.seletecd.setOnClickListener{
//            WMSSharedPref.getAppPrefs(
//                WMSApplication.getInstance().context
//            ).clearSharedPrefs("PERPETUAL_COMB_LIST1_AB_SINGLE")
//            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
//                .saveSingleStock(data[position])
//            val myIntent = Intent(ctx, PerpetualConfirmActivity::class.java)
//            ctx.startActivity(myIntent)
//        }

    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return data.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val bin_Location = itemView.txt_cell_1
        val itemcode = itemView.txt_cell_2
        val partNo = itemView.txt_cell_3
        val seletecd = itemView.txt_cell_5



        fun bindItems(model: PerpetualLine) {

            bin_Location.text = model.storageBin

            itemcode.text = model.itemCode
            partNo.text = model.manufacturerName
            //seletecd.isChecked = model.isSelected == true


        }

    }
}