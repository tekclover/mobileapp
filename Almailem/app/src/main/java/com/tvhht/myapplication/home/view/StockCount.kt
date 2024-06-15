package com.tvhht.myapplication.home.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.tvhht.myapplication.R
import com.tvhht.myapplication.annual.AnnualListActivity
import com.tvhht.myapplication.stock.PerpetualListActivity
import com.tvhht.myapplication.utils.PrefConstant
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


        val count1 = rootView.findViewById<TextView>(R.id.count1)
        count1.text = countNO?.stockCount?.perpertual.toString()

        val count2 = rootView.findViewById<TextView>(R.id.count2)
        count2.text = countNO?.stockCount?.periodic.toString()


        if (userInfoSingle!!.inventory) {
            perpectual.visibility = View.VISIBLE
            annualStock.visibility = View.VISIBLE
        }
        else {
            perpectual.visibility = View.GONE
            annualStock.visibility = View.GONE
        }


        perpectual.setOnClickListener {

            WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
            ).saveIntValue(PrefConstant.HOME_PAGE_INDEX, 3)

            val myIntent = Intent(context, PerpetualListActivity::class.java)
            requireContext().startActivity(myIntent)
        }


        annualStock.setOnClickListener {

            WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
            ).saveIntValue(PrefConstant.HOME_PAGE_INDEX, 3)

            val myIntent = Intent(context, AnnualListActivity::class.java)
            requireContext().startActivity(myIntent)
        }

        return rootView
    }
}