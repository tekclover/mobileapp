package com.tvhht.myapplication.home.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.tvhht.myapplication.R
import com.tvhht.myapplication.reports.ReportListFromBinNoActivity
import com.tvhht.myapplication.reports.ReportListFromItemCodeActivity

import com.tvhht.myapplication.reports.ReportListFromPalletIDActivity
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref

class Reports : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.list_home_cell_reports, container, false)


        var id_for_bin = rootView.findViewById<LinearLayout>(R.id.id_for_bin)
        var id_for_pallet = rootView.findViewById<LinearLayout>(R.id.id_for_pallet)
        var id_for_item = rootView.findViewById<LinearLayout>(R.id.id_for_item)


        id_for_bin.setOnClickListener {

            WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
            ).saveIntValue(PrefConstant.HOME_PAGE_INDEX, 4)
            val myIntent = Intent(context, ReportListFromBinNoActivity::class.java)
            requireContext().startActivity(myIntent)
        }


        id_for_pallet.setOnClickListener {

            WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
            ).saveIntValue(PrefConstant.HOME_PAGE_INDEX, 4)
            val myIntent = Intent(context, ReportListFromPalletIDActivity::class.java)
            requireContext().startActivity(myIntent)
        }


        id_for_item.setOnClickListener {
            WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
            ).saveIntValue(PrefConstant.HOME_PAGE_INDEX, 4)

            val myIntent = Intent(context, ReportListFromItemCodeActivity::class.java)
            requireContext().startActivity(myIntent)

        }



        return rootView
    }
}