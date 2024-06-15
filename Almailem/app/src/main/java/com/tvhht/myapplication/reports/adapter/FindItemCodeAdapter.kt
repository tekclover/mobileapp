package com.tvhht.myapplication.reports.adapter

import android.content.Context
import android.widget.ArrayAdapter

class FindItemCodeAdapter(context: Context, resource: Int) :
    ArrayAdapter<String>(context, resource) {
    private val itemCodeList: MutableList<String>

    init {
        itemCodeList = ArrayList()
    }

    fun updateAdapter(updateList: List<String>) {
        itemCodeList.clear()
        itemCodeList.addAll(updateList)
    }

    override fun getCount(): Int {
        return itemCodeList.size
    }

    override fun getItem(position: Int): String {
        return itemCodeList[position]
    }
}
