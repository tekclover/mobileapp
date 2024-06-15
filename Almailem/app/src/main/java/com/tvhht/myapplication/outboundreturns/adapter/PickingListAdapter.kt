package com.tvhht.myapplication.outboundreturns.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tvhht.myapplication.R
import com.tvhht.myapplication.picking.model.PickingListResponse
import com.tvhht.myapplication.putaway.InfoCustomDialog
import com.tvhht.myapplication.utils.ApiConstant
import kotlinx.android.synthetic.main.list_picking_details_cell.view.txt_cell_1
import kotlinx.android.synthetic.main.list_picking_details_cell.view.txt_cell_2
import kotlinx.android.synthetic.main.list_picking_details_cell.view.txt_cell_3
import kotlinx.android.synthetic.main.list_picking_details_cell.view.txt_cell_4


class PickingListAdapter(val ctx: Context, val data: List<PickingListResponse>,
                         private val onOderSelection: (orderNo:String,orderType:String) -> Unit) :
    RecyclerView.Adapter<PickingListAdapter.ViewHolder>() {


    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_picking_details_cell, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(data[position])

        holder.partNo.setOnClickListener {

            val cdd = InfoCustomDialog(
                ctx,
                "",
                data[position].manufacturerName?:"",
                data[position].itemCode?:"",
                data[position].description?:"",
            )
            cdd.show()
        }

        holder.itemView.txt_cell_4.setOnClickListener {

           /* WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
            ).savePickingInfo(data[position])

            val myIntent = Intent(ctx, ReturnPickingDetailsActivity::class.java)
            myIntent.putExtra("DEFAULT_BIN_NO",data[position].proposedStorageBin)
            ctx.startActivity(myIntent)*/

            holder.itemView.txt_cell_4.isChecked = false
            data[position].refDocNumber?.let { it1 -> data[position].referenceDocumentType?.let { it2 ->
                onOderSelection.invoke(it1,
                    it2
                )
            } }
        }
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return data.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


       /* val bin_no = itemView.txt_cell_1
        val sfr_mku = itemView.txt_cell_2
        val qty = itemView.txt_cell_3*/
        val picker = itemView.txt_cell_1
        val orderType = itemView.txt_cell_2
        val partNo = itemView.txt_cell_3
        val binNo = itemView.txt_cell_4

        fun bindItems(model: PickingListResponse) {
           /* sfr_mku.text = model.itemCode
            bin_no.text = model.proposedStorageBin
            qty.text = model.pickToQty.toString()*/
            picker.text = model.assignedPickerId
            orderType.text = model.referenceDocumentType
            partNo.text = if (model.referenceDocumentType == ApiConstant.PICK_LIST) model.salesOrderNumber else model.refDocNumber
            binNo.text = model.proposedStorageBin
        }
    }
}