package com.tvhht.myapplication.cases.adapter


import android.content.Context
import android.content.Intent

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.tvhht.myapplication.R
import com.tvhht.myapplication.cases.ASNNumberListActivity
import com.tvhht.myapplication.cases.model.CaseDetailModel

import kotlinx.android.synthetic.main.list_case_details_cell.view.*

class CaseDetailAdapter(val ctx: Context, val data: List<CaseDetailModel>) :
    RecyclerView.Adapter<CaseDetailAdapter.ViewHolder>() {


    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CaseDetailAdapter.ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.list_case_details_cell, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: CaseDetailAdapter.ViewHolder, position: Int) {
        holder.bindItems(data!![position])
       holder.sNO.text = (position+1).toString()

    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return data!!.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val sNO = itemView.txt_cell_1
        fun bindItems(model: CaseDetailModel) {

            val asnNO = itemView.txt_cell_2
            val sDate = itemView.txt_cell_3

            asnNO.text = model.caseCode
            sDate.isChecked=model.isSelected

        }

    }
}