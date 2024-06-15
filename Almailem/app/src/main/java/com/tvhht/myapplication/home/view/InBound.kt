package com.tvhht.myapplication.home.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tvhht.myapplication.databinding.ListHomeCellsInboundBinding
import com.tvhht.myapplication.goodsreceipt.GoodsReceiptSelectedActivity
import com.tvhht.myapplication.inboundreversal.InboundReversalListActivity
import com.tvhht.myapplication.putaway.PutAwayListActivity
import com.tvhht.myapplication.utils.PrefConstant
import com.tvhht.myapplication.utils.WMSApplication
import com.tvhht.myapplication.utils.WMSSharedPref


class InBound : Fragment() {
    private lateinit var binding: ListHomeCellsInboundBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ListHomeCellsInboundBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userInfoSingle = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).userInfoSingle

        val countNo = WMSSharedPref.getAppPrefs(
            WMSApplication.getInstance().context
        ).countInfo

        with(binding) {
            count1.text = countNo?.inboundCount?.cases.toString()
            count2.text = countNo?.inboundCount?.putaway.toString()
            count3.text = countNo?.inboundCount?.reversals.toString()

            itemCell1.visibility =
                if (userInfoSingle?.caseReceipts == true) View.VISIBLE else View.GONE
            itemCell2.visibility = if (userInfoSingle?.putaway == true) View.VISIBLE else View.GONE
            itemCell3.visibility =
                if (userInfoSingle?.supplierReturn == true) View.VISIBLE else View.GONE
        }
        setListener()
    }

    private fun setListener() {
        with(binding) {
            itemCell1.setOnClickListener {
                WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).saveIntValue(PrefConstant.HOME_PAGE_INDEX, 0)

                val myIntent = Intent(context, GoodsReceiptSelectedActivity::class.java)
                requireContext().startActivity(myIntent)
            }

            itemCell2.setOnClickListener {
                WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).saveIntValue(PrefConstant.HOME_PAGE_INDEX, 0)

                val myIntent = Intent(context, PutAwayListActivity::class.java)
                requireContext().startActivity(myIntent)
            }

            itemCell3.setOnClickListener {
                WMSSharedPref.getAppPrefs(
                    WMSApplication.getInstance().context
                ).saveIntValue(PrefConstant.HOME_PAGE_INDEX, 0)

                val myIntent = Intent(context, InboundReversalListActivity::class.java)
                requireContext().startActivity(myIntent)
            }
        }
    }
}