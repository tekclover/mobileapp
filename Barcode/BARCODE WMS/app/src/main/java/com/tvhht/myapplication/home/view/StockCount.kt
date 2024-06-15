package com.tvhht.myapplication.home.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.tvhht.myapplication.R
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref

class StockCount : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
            View? {
        val rootView = inflater.inflate(R.layout.list_home_cells_stocks, container, false)


        var perpectual = rootView.findViewById<LinearLayout>(R.id.item_cell_1)
        var annualStock = rootView.findViewById<LinearLayout>(R.id.item_cell_2)


        val userInfoSingle = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).userInfoSingle

        val countNO = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).countInfo

        var count1 = rootView.findViewById<TextView>(R.id.count1)

        if (countNO?.stockCount == null) {
            count1.text = "0"
        } else {
            count1.text = countNO?.stockCount.toString()
        }

        if (userInfoSingle!!.inventory) {
            perpectual.visibility = View.VISIBLE
            annualStock.visibility = View.VISIBLE
        }
        else {
            perpectual.visibility = View.GONE
            annualStock.visibility = View.GONE
        }





        return rootView
    }
}