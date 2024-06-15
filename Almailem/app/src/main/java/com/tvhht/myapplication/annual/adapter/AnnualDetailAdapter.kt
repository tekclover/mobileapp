package com.tvhht.myapplication.annual.adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tvhht.myapplication.R
import com.tvhht.myapplication.annual.AnnualConfirmActivity
import com.tvhht.myapplication.annual.model.PeriodicLine
import com.tvhht.myapplication.putaway.InfoCustomDialog
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import kotlinx.android.synthetic.main.list_perpetual_detail_cell.view.*


class AnnualDetailAdapter(val ctx: Context, val data: List<PeriodicLine>) :
    RecyclerView.Adapter<AnnualDetailAdapter.ViewHolder>() {


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

        holder.partNo.setOnClickListener {

            val cdd = InfoCustomDialog(
                ctx,
                "",
                data[position].referenceField9,
                data[position].itemCode,
                data[position].referenceField8,
            )
            cdd.show()
        }


//        holder.seletecd.setOnClickListener{
//            WMSSharedPref.getAppPrefs(
//                WMSApplication.getInstance().context
//            ).clearSharedPrefs("STOCKS_QUALITY_INFO_LIST_PER2")
//            WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
//                .saveSingleStockAnnual(data[position])
//            val myIntent = Intent(ctx, AnnualConfirmActivity::class.java)
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



        fun bindItems(model: PeriodicLine) {

            bin_Location.text = model.storageBin
            itemcode.text = model.itemCode
            partNo.text = model.manufacturerName
            //seletecd.isChecked = model.isSelected == true


        }

    }
}