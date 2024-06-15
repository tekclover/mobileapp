package com.tvhht.myapplication.login

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.tvhht.myapplication.R
import com.tvhht.myapplication.login.model.UserDetails

class BranchAdapter(private val ctx: Context, private val plantList: List<UserDetails>) :
    BaseAdapter() {
    override fun getCount(): Int {
        return plantList.size
    }

    override fun getItem(position: Int): Any {
        return plantList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(ctx)
            .inflate(R.layout.adapter_spinner_view, parent, false)
        val plantId: TextView = view.findViewById(R.id.text_plant_id)
        plantList[position].plantIdAndDescription?.let {
            plantId.text = it
        }
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getView(position, convertView, parent)
    }
}