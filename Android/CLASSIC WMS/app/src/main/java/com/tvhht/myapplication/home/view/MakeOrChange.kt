package com.tvhht.myapplication.home.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.tvhht.myapplication.R
import com.tvhht.myapplication.quality.QualityListActivity
import com.tvhht.myapplication.quality.utils.HECustomDialog


import com.tvhht.myapplication.transfers.utils.SearchBinCustomDialog
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref

class MakeOrChange : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.list_home_cells_make_change, container, false)


        var transfers = rootView.findViewById<LinearLayout>(R.id.item_cell_1)


        transfers.setOnClickListener {

            WMSSharedPref.getAppPrefs(
                WMSApplication.getInstance().context
            ).saveIntValue(PrefConstant.HOME_PAGE_INDEX, 1)


            val myIntent = Intent(context, SearchBinCustomDialog::class.java)
            requireContext().startActivity(myIntent)
        }




        return rootView
    }
}