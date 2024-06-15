package com.tvhht.myapplication.quality.adapter


import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tvhht.myapplication.R
import com.tvhht.myapplication.putaway.InfoCustomDialog
import com.tvhht.myapplication.quality.model.QualityDetailsModel
import com.tvhht.myapplication.quality.utils.QualityCustomDialogActivity
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import kotlinx.android.synthetic.main.list_quality_details_confirm_cell.view.*


class QualityDetailsAdapter(val ctx: Context, val data: List<QualityDetailsModel>) :
    RecyclerView.Adapter<QualityDetailsAdapter.ViewHolder>() {


    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QualityDetailsAdapter.ViewHolder {
        val v =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_quality_details_confirm_cell, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: QualityDetailsAdapter.ViewHolder, position: Int) {
        holder.bindItems(data!![position])


        holder.isselected.setOnClickListener {

//            val cdd = PalletIDCustomDialog(
//                ctx,
//                "Verify Pallet ID",
//                data!![position]
//            )
//            cdd.show()


            WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().applicationContext
            ).saveQualityInfo(data!![position])
            val myIntent = Intent(ctx, QualityCustomDialogActivity::class.java)
            ctx.startActivity(myIntent)
        }


        holder.pallet.setOnClickListener {

            val cdd = InfoCustomDialog(
                ctx,
                "",
                data[position].manufacturerPartNo,
                data[position].itemCode,
                data[position].description
            )
            cdd.show()
        }


        holder.sno.setOnClickListener {

            displayPopupWindow(it, "Partner Code: "+data[position].partnerCode.toString())
        }

    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return data!!.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val sno = itemView.txt_cell_1
        val pallet = itemView.txt_cell_2
        val type = itemView.txt_cell_3
        val qty = itemView.txt_cell_4
        val ins_qty = itemView.txt_cell_5
        val isselected = itemView.txt_cell_6
        fun bindItems(model: QualityDetailsModel) {


            sno.text = model.refDocNumber
            pallet.text = model.itemCode

            qty.text = model.pickConfirmQty.toString()
            ins_qty.text = model.pickQty
            isselected.isChecked = model.isSelected

        }

    }


    private fun displayPopupWindow(anchorView: View, msg: String) {
        val popup: PopupWindow = PopupWindow(ctx)
        val layout: View = LayoutInflater.from(ctx)
            .inflate(R.layout.popup_content, null)

        popup.contentView = layout
        val txt = layout.findViewById<TextView>(R.id.text_display)
        txt.text = msg
        popup.height = WindowManager.LayoutParams.WRAP_CONTENT
        popup.width = WindowManager.LayoutParams.WRAP_CONTENT
        popup.isOutsideTouchable = true
        popup.isFocusable = true
        popup.setBackgroundDrawable(BitmapDrawable())
        popup.showAsDropDown(anchorView)
    }
}