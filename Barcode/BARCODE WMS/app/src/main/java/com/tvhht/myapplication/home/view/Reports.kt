package com.tvhht.myapplication.home.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.tvhht.myapplication.R

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






        return rootView
    }
}