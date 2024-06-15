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


class InBound : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.list_home_cells_inbound, container, false)

        var casesMenu = rootView.findViewById<LinearLayout>(R.id.item_cell_1)
        var putawayMenu = rootView.findViewById<LinearLayout>(R.id.item_cell_2)
        var reversalMenu = rootView.findViewById<LinearLayout>(R.id.item_cell_3)

        val userInfoSingle = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).userInfoSingle


        val countNO = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).countInfo

        var count1 = rootView.findViewById<TextView>(R.id.count1)
        count1.text = countNO?.inboundCount?.cases.toString()

        var count2 = rootView.findViewById<TextView>(R.id.count2)
        count2.text = countNO?.inboundCount?.putaway.toString()

        var count3 = rootView.findViewById<TextView>(R.id.count3)
        count3.text = countNO?.inboundCount?.reversals.toString()

        if(userInfoSingle!!.caseReceipts)
            casesMenu.visibility=View.VISIBLE
        else
            casesMenu.visibility=View.GONE

        if(userInfoSingle!!.putaway)
            putawayMenu.visibility=View.VISIBLE
        else
            putawayMenu.visibility=View.GONE

        if(userInfoSingle!!.supplierReturn)
            reversalMenu.visibility=View.VISIBLE
        else
            reversalMenu.visibility=View.GONE



        return rootView
    }

}