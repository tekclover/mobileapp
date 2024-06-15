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

class OutBound : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.list_home_cells_outbound, container, false)


        var picking = rootView.findViewById<LinearLayout>(R.id.item_cell_1)
        var quality = rootView.findViewById<LinearLayout>(R.id.item_cell_2)
        var reversalMenu = rootView.findViewById<LinearLayout>(R.id.item_cell_3)

        val userInfoSingle = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).userInfoSingle

        val countNO = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).countInfo

        var count1 = rootView.findViewById<TextView>(R.id.count1)
        count1.text = countNO?.outboundCount?.picking.toString()

        var count2 = rootView.findViewById<TextView>(R.id.count2)
        count2.text = countNO?.outboundCount?.quality.toString()

        var count3 = rootView.findViewById<TextView>(R.id.count3)
        count3.text = countNO?.outboundCount?.reversals.toString()


        if(userInfoSingle!!.picking)
            picking.visibility=View.VISIBLE
        else
            picking.visibility=View.GONE

        if(userInfoSingle!!.quality)
            quality.visibility=View.VISIBLE
        else
            quality.visibility=View.GONE

        if(userInfoSingle!!.customerReturn)
            reversalMenu.visibility=View.VISIBLE
        else
            reversalMenu.visibility=View.GONE





        return rootView
    }

}