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
import com.tvhht.myapplication.cases.ASNNumberListActivity
import com.tvhht.myapplication.inboundreversal.InboundReversalListActivity
import com.tvhht.myapplication.putaway.PutAwayListActivity
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref
import kotlinx.android.synthetic.main.list_home_cells_inbound.*
import org.w3c.dom.Text


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


        casesMenu.setOnClickListener {

            WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
            ).saveIntValue(PrefConstant.HOME_PAGE_INDEX, 0)

            val myIntent = Intent(context, ASNNumberListActivity::class.java)
            requireContext().startActivity(myIntent)
        }


        putawayMenu.setOnClickListener {

            WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
            ).saveIntValue(PrefConstant.HOME_PAGE_INDEX, 0)

            val myIntent = Intent(context, PutAwayListActivity::class.java)
            requireContext().startActivity(myIntent)
        }


        reversalMenu.setOnClickListener {

            WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
            ).saveIntValue(PrefConstant.HOME_PAGE_INDEX, 0)

            val myIntent = Intent(context, InboundReversalListActivity::class.java)
            requireContext().startActivity(myIntent)
        }



        return rootView
    }

}