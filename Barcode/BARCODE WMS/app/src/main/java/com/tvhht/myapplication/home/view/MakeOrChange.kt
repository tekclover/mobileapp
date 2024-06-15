package com.tvhht.myapplication.home.view


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.tvhht.myapplication.R

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







        return rootView
    }
}