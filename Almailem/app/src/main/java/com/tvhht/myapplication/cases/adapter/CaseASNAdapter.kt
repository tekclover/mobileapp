package com.tvhht.myapplication.cases.adapter


import android.content.Context
import android.content.Intent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tvhht.myapplication.R
import com.tvhht.myapplication.cases.CaseDetailActivity
import com.tvhht.myapplication.cases.model.AsnList
import com.tvhht.myapplication.utils.DateUtil

import kotlinx.android.synthetic.main.list_asn_no_cell.view.*

class CaseASNAdapter(val ctx: Context, var data: List<AsnList>) :
    RecyclerView.Adapter<CaseASNAdapter.ViewHolder>() {


    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CaseASNAdapter.ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.list_asn_no_cell, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: CaseASNAdapter.ViewHolder, position: Int) {
        holder.bindItems(data!![position])
        holder.sNO.text= (position+1).toString()
        holder.itemView.setOnClickListener {
            val intent = Intent(ctx, CaseDetailActivity::class.java)
            intent.putExtra("CASE_LIST_SEL", data[position])
            intent.putExtra("CASE_LIST_SEL_ID", data[position].refDocNumber)
            ctx.startActivity(intent)
        }

    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return data!!.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         var sNO = itemView.txt_cell_1;

        fun bindItems(model: AsnList) {
            val asnNO = itemView.txt_cell_2
            val sDate = itemView.txt_cell_3
            asnNO.text = model.refDocNumber
            sDate.text = DateUtil.getDateYYYYMMDD(model.createdOn)

        }

    }

    fun filterList(filterllist: List<AsnList?>) {
        // below line is to add our filtered
        // list in our course array list.
        data = filterllist as List<AsnList>
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }
}