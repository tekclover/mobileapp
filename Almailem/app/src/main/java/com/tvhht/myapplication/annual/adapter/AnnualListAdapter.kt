package com.tvhht.myapplication.annual.adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tvhht.myapplication.R
import com.tvhht.myapplication.annual.AnnualDetailsActivity
import com.tvhht.myapplication.annual.model.AnnualStockResponse
import com.tvhht.myapplication.stock.PerpetualDetailsActivity
import com.tvhht.myapplication.stock.model.PerpetualResponse
import com.tvhht.myapplication.utils.*
import kotlinx.android.synthetic.main.list_perpetual_list_cell.view.*


class AnnualListAdapter(val ctx: Context, val data: List<AnnualStockResponse>) :
    RecyclerView.Adapter<AnnualListAdapter.ViewHolder>() {


    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_annual_list_cell, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(data[position])



        holder.itemView.setOnClickListener {


                WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).saveStockInfo2(data[position])
                WMSSharedPref.getAppPrefs(WMSApplication.getInstance().context)
                    .saveStringValue(PrefConstant.STOCK_INDEX_SEL, data[position].cycleCountNo)
                val myIntent = Intent(ctx, AnnualDetailsActivity::class.java)
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


        fun bindItems(model: AnnualStockResponse) {
            qty.text = DateUtil.getDateYYYYMMDD(model.createdOn)
            bin_no.text = model.cycleCountNo
            sfr_mku.text=model.referenceCycleCountNo
        }

    }
}