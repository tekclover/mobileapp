package com.tvhht.myapplication.quality.adapter


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tvhht.myapplication.R
import com.tvhht.myapplication.cases.ASNNumberListActivity
import com.tvhht.myapplication.quality.model.QualityListResponse
import com.tvhht.myapplication.quality.utils.HECustomDialog
import com.tvhht.myapplication.utils.DateUtil
import kotlinx.android.synthetic.main.list_quality_details_cell.view.*

class QualityAdapter(val ctx: Context, val data: List<QualityListResponse>) :
    RecyclerView.Adapter<QualityAdapter.ViewHolder>() {


    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QualityAdapter.ViewHolder {
        val v =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_quality_details_cell, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: QualityAdapter.ViewHolder, position: Int) {
        holder.bindItems(data[position])
        val iValue = position + 1;
        holder.sno.text = iValue.toString()

        holder.isselected.setOnClickListener {

            holder.isselected.isChecked=false
            val myIntent =
                Intent(ctx, HECustomDialog::class.java)
            myIntent.putExtra("EXTRA_CODE_1", data[position].actualHeNo)
            myIntent.putExtra("EXTRA_CODE_2",data[position].pickupNumber)
            myIntent.putExtra("EXTRA_CODE_3",data[position].qualityInspectionNo)
            myIntent.putExtra("EXTRA_CODE_4","Verify HE Number")
            ctx.startActivity(myIntent)
        }

    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return data.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val sno = itemView.txt_cell_1
        val he_no = itemView.txt_cell_2
        val date = itemView.txt_cell_3
        val isselected = itemView.txt_cell_4

        fun bindItems(model: QualityListResponse) {

            he_no.text = model.actualHeNo
            date.text = DateUtil.getDateYYYYMMDD(model.qualityCreatedOn)
            isselected.isChecked = model.isSelected

        }

    }
}